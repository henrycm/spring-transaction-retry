package io.concurrent.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestService
{
    @Autowired
    TestRepository repository;

    @Retryable(backoff = @Backoff(delay = 300), maxAttempts = 5)
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TestEntity createNext( String name )
    {
        TestEntity entity = repository.findTop1ByOrderByIdDesc();

        TestEntity newEntity = new TestEntity();
        newEntity.setId( entity != null ? entity.getId() + 1 : 1 );
        newEntity.setName( name );

        return repository.save( newEntity );
    }

}
