package com.whl.spring.demo.commons_pool2;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.time.Duration;
import java.util.Random;

public class ReaderUtil {
    private ObjectPool<StringBuffer> pool;

    public ReaderUtil(ObjectPool<StringBuffer> pool) {
        this.pool = pool;
    }

    /**
     * Dumps the contents of the {@link Reader} to a String, closing the {@link Reader} when done.
     */
    public String readToString(Reader in)
            throws IOException {
        StringBuffer buf = null;
        try {
            buf = pool.borrowObject();
            for (int c = in.read(); c != -1; c = in.read()) {
                buf.append((char) c);
            }
            Thread.sleep(new Random().nextInt(1000));
            return buf.toString();
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Unable to borrow buffer from pool" + e.toString());
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                // ignored
            }
            try {
                if (null != buf) {
                    pool.returnObject(buf);
                }
            } catch (Exception e) {
                // ignored
            }
        }
    }

    public static void main(String[] args) {
        StringBufferFactory factory = new StringBufferFactory();
        GenericObjectPoolConfig<StringBuffer> config = new GenericObjectPoolConfig<StringBuffer>();
        config.setMinIdle(5);
        config.setMaxIdle(5);
        config.setMaxTotal(20);
        config.setMaxWait(Duration.ofSeconds(2));
        ReaderUtil readerUtil = new ReaderUtil(new GenericObjectPool<StringBuffer>(factory, config));
        Thread[] threads = new Thread[100];

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new ReadTask(readerUtil, i));
        }

        for (Thread thread : threads) {
            thread.start();
        }
    }

    static class ReadTask implements Runnable {
        private final ReaderUtil readerUtil;

        private final int index;

        ReadTask(ReaderUtil readerUtil, int index) {
            this.readerUtil = readerUtil;
            this.index = index;
        }

        @Override
        public void run() {
            try {
                String original = RandomStringUtils.randomAlphanumeric(100);
                String actual = this.readerUtil.readToString(new CharArrayReader(original.toCharArray()));
                System.out.println("Thread " + String.format("%02d", this.index) + " -> readToString succeed");
            } catch (RuntimeException e) {
                System.out.println("Thread " + String.format("%02d", this.index) + " -> readToString failed: " + e.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
