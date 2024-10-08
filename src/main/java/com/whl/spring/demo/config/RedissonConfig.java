package com.whl.spring.demo.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Sentinel;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class RedissonConfig {
    private static final String REDIS_PROTOCOL_PREFIX = "redis://";

    private static final String REDISS_PROTOCOL_PREFIX = "rediss://";

    @Autowired
    private RedisProperties redisProperties;

    @Autowired
    private ApplicationContext ctx;

    private boolean hasConnectionDetails() {
        try {
            Class.forName("org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(RedissonClient.class)
    public RedissonClient redisson() throws IOException {
        Config config;
        Method clusterMethod = ReflectionUtils.findMethod(RedisProperties.class, "getCluster");
        Method usernameMethod = ReflectionUtils.findMethod(RedisProperties.class, "getUsername");
        Method timeoutMethod = ReflectionUtils.findMethod(RedisProperties.class, "getTimeout");
        Method connectTimeoutMethod = ReflectionUtils.findMethod(RedisProperties.class, "getConnectTimeout");
        Method clientNameMethod = ReflectionUtils.findMethod(RedisProperties.class, "getClientName");

        Object timeoutValue = ReflectionUtils.invokeMethod(timeoutMethod, redisProperties);
        String prefix = getPrefix();

        String username = null;
        int database = redisProperties.getDatabase();
        String password = redisProperties.getPassword();
        boolean isSentinel = false;
        boolean isCluster = false;
        if (hasConnectionDetails()) {
            ObjectProvider<RedisConnectionDetails> provider = ctx.getBeanProvider(RedisConnectionDetails.class);
            RedisConnectionDetails b = provider.getIfAvailable();
            if (b != null) {
                password = b.getPassword();
                username = b.getUsername();

                if (b.getSentinel() != null) {
                    isSentinel = true;
                }
                if (b.getCluster() != null) {
                    isCluster = true;
                }
            }
        }

        Integer timeout = null;
        if (timeoutValue instanceof Duration) {
            timeout = (int) ((Duration) timeoutValue).toMillis();
        } else if (timeoutValue != null){
            timeout = (Integer) timeoutValue;
        }

        Integer connectTimeout = null;
        if (connectTimeoutMethod != null) {
            Object connectTimeoutValue = ReflectionUtils.invokeMethod(connectTimeoutMethod, redisProperties);
            if (connectTimeoutValue != null) {
                connectTimeout = (int) ((Duration) connectTimeoutValue).toMillis();
            }
        } else {
            connectTimeout = timeout;
        }

        String clientName = null;
        if (clientNameMethod != null) {
            clientName = (String) ReflectionUtils.invokeMethod(clientNameMethod, redisProperties);
        }

        if (usernameMethod != null) {
            username = (String) ReflectionUtils.invokeMethod(usernameMethod, redisProperties);
        }

        if (redisProperties.getSentinel() != null || isSentinel) {
            String[] nodes = {};
            String sentinelMaster = null;

            if (redisProperties.getSentinel() != null) {
                Method nodesMethod = ReflectionUtils.findMethod(Sentinel.class, "getNodes");
                Object nodesValue = ReflectionUtils.invokeMethod(nodesMethod, redisProperties.getSentinel());
                if (nodesValue instanceof String) {
                    nodes = convert(prefix, Arrays.asList(((String) nodesValue).split(",")));
                } else {
                    nodes = convert(prefix, (List<String>) nodesValue);
                }
                sentinelMaster = redisProperties.getSentinel().getMaster();
            }


            String sentinelUsername = null;
            String sentinelPassword = null;
            if (hasConnectionDetails()) {
                ObjectProvider<RedisConnectionDetails> provider = ctx.getBeanProvider(RedisConnectionDetails.class);
                RedisConnectionDetails b = provider.getIfAvailable();
                if (b != null && b.getSentinel() != null) {
                    database = b.getSentinel().getDatabase();
                    sentinelMaster = b.getSentinel().getMaster();
                    nodes = convertNodes(prefix, (List<Object>) (Object) b.getSentinel().getNodes());
                    sentinelUsername = b.getSentinel().getUsername();
                    sentinelPassword = b.getSentinel().getPassword();
                }
            }

            config = new Config();
            SentinelServersConfig c = config.useSentinelServers()
                    .setMasterName(sentinelMaster)
                    .addSentinelAddress(nodes)
                    .setSentinelPassword(sentinelPassword)
                    .setSentinelUsername(sentinelUsername)
                    .setDatabase(database)
                    .setUsername(username)
                    .setPassword(password)
                    .setClientName(clientName);
            if (connectTimeout != null) {
                c.setConnectTimeout(connectTimeout);
            }
            if (connectTimeoutMethod != null && timeout != null) {
                c.setTimeout(timeout);
            }
            initSSL(c);
        } else if ((clusterMethod != null && ReflectionUtils.invokeMethod(clusterMethod, redisProperties) != null)
                    || isCluster) {

            String[] nodes = {};
            if (clusterMethod != null && ReflectionUtils.invokeMethod(clusterMethod, redisProperties) != null) {
                Object clusterObject = ReflectionUtils.invokeMethod(clusterMethod, redisProperties);
                Method nodesMethod = ReflectionUtils.findMethod(clusterObject.getClass(), "getNodes");
                List<String> nodesObject = (List) ReflectionUtils.invokeMethod(nodesMethod, clusterObject);

                nodes = convert(prefix, nodesObject);
            }

            if (hasConnectionDetails()) {
                ObjectProvider<RedisConnectionDetails> provider = ctx.getBeanProvider(RedisConnectionDetails.class);
                RedisConnectionDetails b = provider.getIfAvailable();
                if (b != null && b.getCluster() != null) {
                    nodes = convertNodes(prefix, (List<Object>) (Object) b.getCluster().getNodes());
                }
            }

            config = new Config();
            ClusterServersConfig c = config.useClusterServers()
                    .addNodeAddress(nodes)
                    .setUsername(username)
                    .setPassword(password)
                    .setClientName(clientName);
            if (connectTimeout != null) {
                c.setConnectTimeout(connectTimeout);
            }
            if (connectTimeoutMethod != null && timeout != null) {
                c.setTimeout(timeout);
            }
            initSSL(c);
        } else {
            config = new Config();

            String singleAddr = prefix + redisProperties.getHost() + ":" + redisProperties.getPort();

            if (hasConnectionDetails()) {
                ObjectProvider<RedisConnectionDetails> provider = ctx.getBeanProvider(RedisConnectionDetails.class);
                RedisConnectionDetails b = provider.getIfAvailable();
                if (b != null && b.getStandalone() != null) {
                    database = b.getStandalone().getDatabase();
                    singleAddr = prefix + b.getStandalone().getHost() + ":" + b.getStandalone().getPort();
                }
            }

            SingleServerConfig c = config.useSingleServer()
                    .setAddress(singleAddr)
                    .setDatabase(database)
                    .setUsername(username)
                    .setPassword(password)
                    .setClientName(clientName);
            if (connectTimeout != null) {
                c.setConnectTimeout(connectTimeout);
            }
            if (connectTimeoutMethod != null && timeout != null) {
                c.setTimeout(timeout);
            }
            initSSL(c);
        }
        return Redisson.create(config);
    }

    private void initSSL(BaseConfig<?> config) {
        Method getSSLMethod = ReflectionUtils.findMethod(RedisProperties.class, "getSsl");
        if (getSSLMethod == null) {
            return;
        }

        RedisProperties.Ssl ssl = redisProperties.getSsl();
        if (ssl.getBundle() == null) {
            return;
        }

        ObjectProvider<SslBundles> provider = ctx.getBeanProvider(SslBundles.class);
        SslBundles bundles = provider.getIfAvailable();
        if (bundles == null) {
            return;
        }
        SslBundle b = bundles.getBundle(ssl.getBundle());
        if (b == null) {
            return;
        }
        config.setSslCiphers(b.getOptions().getCiphers());
        config.setSslProtocols(b.getOptions().getEnabledProtocols());
        config.setSslTrustManagerFactory(b.getManagers().getTrustManagerFactory());
        config.setSslKeyManagerFactory(b.getManagers().getKeyManagerFactory());
    }

    private String getPrefix() {
        String prefix = REDIS_PROTOCOL_PREFIX;
        Method isSSLMethod = ReflectionUtils.findMethod(RedisProperties.class, "isSsl");
        Method getSSLMethod = ReflectionUtils.findMethod(RedisProperties.class, "getSsl");
        if (isSSLMethod != null) {
            if ((Boolean) ReflectionUtils.invokeMethod(isSSLMethod, redisProperties)) {
                prefix = REDISS_PROTOCOL_PREFIX;
            }
        } else if (getSSLMethod != null) {
            Object ss = ReflectionUtils.invokeMethod(getSSLMethod, redisProperties);
            if (ss != null) {
                Method isEnabledMethod = ReflectionUtils.findMethod(ss.getClass(), "isEnabled");
                Boolean enabled = (Boolean) ReflectionUtils.invokeMethod(isEnabledMethod, ss);
                if (enabled) {
                    prefix = REDISS_PROTOCOL_PREFIX;
                }
            }
        }
        return prefix;
    }

    private String[] convertNodes(String prefix, List<Object> nodesObject) {
        List<String> nodes = new ArrayList<>(nodesObject.size());
        for (Object node : nodesObject) {
            Field hostField = ReflectionUtils.findField(node.getClass(), "host");
            Field portField = ReflectionUtils.findField(node.getClass(), "port");
            ReflectionUtils.makeAccessible(hostField);
            ReflectionUtils.makeAccessible(portField);
            String host = (String) ReflectionUtils.getField(hostField, node);
            int port = (int) ReflectionUtils.getField(portField, node);
            nodes.add(prefix + host + ":" + port);
        }
        return nodes.toArray(new String[0]);
    }

    private String[] convert(String prefix, List<String> nodesObject) {
        List<String> nodes = new ArrayList<>(nodesObject.size());
        for (String node : nodesObject) {
            if (!node.startsWith(REDIS_PROTOCOL_PREFIX) && !node.startsWith(REDISS_PROTOCOL_PREFIX)) {
                nodes.add(prefix + node);
            } else {
                nodes.add(node);
            }
        }
        return nodes.toArray(new String[0]);
    }

}
