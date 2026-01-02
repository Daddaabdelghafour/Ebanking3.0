package com.ebank.account.Commands.configuration;

import com.ebank.account.Commands.aggregate.AccountAggregate;
import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.eventstore.jpa.JpaEventStorageEngine;
import org.axonframework.modelling.command.Repository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AxonJpaConfiguration {

    /**
     * Configure JPA Event Storage Engine to use PostgreSQL
     */
    @Bean
    public EventStore eventStore(JpaEventStorageEngine storageEngine) {
        return EmbeddedEventStore.builder()
                .storageEngine(storageEngine)
                .build();
    }

    /**
     * JPA Event Storage Engine - stores events in PostgreSQL
     */
    @Bean
    public JpaEventStorageEngine storageEngine(EntityManagerProvider entityManagerProvider) {
        return JpaEventStorageEngine.builder()
                .entityManagerProvider(entityManagerProvider)
                .build();
    }

    /**
     * Account Aggregate Repository (Optional - Axon auto-configures this)
     */
    @Bean
    public Repository<AccountAggregate> accountAggregateRepository(EventStore eventStore) {
        return EventSourcingRepository.builder(AccountAggregate.class)
                .eventStore(eventStore)
                .build();
    }
}