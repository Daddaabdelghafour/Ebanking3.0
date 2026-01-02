package com.ebank.account.Commands.configuration;

import com.ebank.account.Commands.aggregate.AccountAggregate;
import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.eventstore.jpa.JpaEventStorageEngine;
import org.axonframework.modelling.command.Repository;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.axonframework.spring.messaging.unitofwork.SpringTransactionManager;

@Configuration
@EntityScan(basePackages = {
    "com.ebank.account",  // Your application entities
    "org.axonframework.eventsourcing.eventstore.jpa",  // Axon event store entities
    "org.axonframework.modelling.saga.repository.jpa"  // Axon saga entities (if using sagas)
})
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
    public JpaEventStorageEngine storageEngine(EntityManagerProvider entityManagerProvider,
                                              PlatformTransactionManager platformTransactionManager) {
        return JpaEventStorageEngine.builder()
                .entityManagerProvider(entityManagerProvider)
                .transactionManager(new SpringTransactionManager(platformTransactionManager))
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