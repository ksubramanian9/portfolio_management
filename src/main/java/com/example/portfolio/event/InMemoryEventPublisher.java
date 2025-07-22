package com.example.portfolio.event;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryEventPublisher implements EventPublisher {

    private final List<Object> events = new CopyOnWriteArrayList<>();

    @Override
    public Mono<Void> publish(Object event) {
        events.add(event);
        return Mono.empty();
    }

    public List<Object> events() {
        return events;
    }
}
