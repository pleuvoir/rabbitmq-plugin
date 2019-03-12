package io.github.pleuvoir.springboot.example.service;

public interface LiveBeginService {

	void update(String liveId) throws LiveBeginException, LiveNotBeginException;
}
