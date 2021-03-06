/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.msc.txn;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
final class SynchronousListener<T> implements Listener<T> {
    private volatile T result;

    public void handleEvent(final T subject) {
        synchronized (this) {
            result = subject;
            notifyAll();
        }
    }

    public T await() throws InterruptedException {
        T result = this.result;
        if (result != null) {
            return result;
        }
        synchronized (this) {
            while (result == null) {
                wait();
            }
            return result;
        }
    }

    public T awaitUninterruptibly() {
        T result = this.result;
        if (result != null) {
            return result;
        }
        boolean intr = false;
        try {
            synchronized (this) {
                while (result == null) try {
                    wait();
                } catch (InterruptedException e) {
                    intr = true;
                }
                return result;
            }
        } finally {
            if (intr) Thread.currentThread().interrupt();
        }
    }
}
