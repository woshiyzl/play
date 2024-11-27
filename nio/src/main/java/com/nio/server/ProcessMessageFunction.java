package com.nio.server;
@FunctionalInterface
public interface ProcessMessageFunction<T, U, R>{
    R apply(T t, U u);
}
