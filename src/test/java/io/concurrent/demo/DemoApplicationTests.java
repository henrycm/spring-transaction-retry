package io.concurrent.demo;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests
{

    @Autowired
    TestService service;

    @Test
    public void contextLoads()
    {
    }

    @Test
    public void testMultipleInserts()
    {
        List<Thread> threads = new LinkedList<>();
        List<TestServiceExecutor> tasks = new LinkedList<>();
        int numThreads = 10;

        for ( int i = 0; i < numThreads; i++ ) {
            TestServiceExecutor task = new TestServiceExecutor( service );
            Thread worker = new Thread( task );
            tasks.add( task );
            threads.add( worker );
        }

        threads.forEach( t -> t.start() );

        for ( Thread thread : threads ) {
            try {
                thread.join();
            } catch ( InterruptedException e ) {
                // OK to leave empty
            }
        }

        assertEquals( numThreads, tasks.stream().filter( t -> t.isSuccessful() ).collect( Collectors.toList() ).size() );
    }

    public class TestServiceExecutor implements Runnable
    {
        boolean successful = false;
        TestService service;

        public TestServiceExecutor( TestService service )
        {
            this.service = service;
        }

        @Override
        public void run()
        {
            TestEntity e = service.createNext( Thread.currentThread().getName() );
            System.out.println( String.format( "Thread [%s] finished. name [%s]", Thread.currentThread().getName(), e.getName() ) );
            successful = true;
        }

        public boolean isSuccessful()
        {
            return successful;
        }

    }
}
