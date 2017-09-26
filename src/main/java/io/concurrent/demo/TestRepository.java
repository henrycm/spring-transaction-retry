package io.concurrent.demo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<TestEntity, Long>
{
    public TestEntity findTop1ByOrderByIdDesc();
}
