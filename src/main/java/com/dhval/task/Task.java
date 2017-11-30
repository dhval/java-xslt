package com.dhval.task;

public abstract class Task {
  public abstract void run() throws Exception;
  public abstract Task init(String config) throws Exception;
}
