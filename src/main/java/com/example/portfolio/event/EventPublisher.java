package com.example.portfolio.event;

import reactor.core.publisher.Mono;

public interface EventPublisher {
    Mono<Void> publish(Object event);
}
