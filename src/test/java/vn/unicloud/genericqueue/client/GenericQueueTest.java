package vn.unicloud.genericqueue.client;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import vn.unicloud.eventbus.protobuf.PingResponse;
import vn.unicloud.genericqueue.protobuf.*;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GenericQueueTest {
    Logger log = LoggerFactory.getLogger(GenericQueueTest.class);
    static final String TOPIC = "test";
    static final String GRPC_CLIENT = "qc";

    @GrpcClient(GRPC_CLIENT)
    private GenericQueueServiceGrpc.GenericQueueServiceBlockingStub stub;
    @GrpcClient(GRPC_CLIENT)
    private TopicServiceGrpc.TopicServiceBlockingStub topicStub;
    List<ProducerMessage> messages;
    @BeforeAll
    public void beforeAll(){
        int totalMessageCount = 10;
        messages = Stream.generate(() ->
                        ProducerMessage.newBuilder()
                                .setId(UUID.randomUUID().toString())
//                                .setPayload(PingResponse
//                                        .newBuilder()
//                                        .setPong(true)
//                                        .build().toByteString())
                                .build())
                .limit(totalMessageCount)
                .collect(Collectors.toList());
    }

    @BeforeEach
    public void beforeEach() {
        topicStub.createTopic(CreateTopicRequest.newBuilder()
                .setTopicName(TOPIC)
                .setTopicSize(1)
                .setSchema(SchemaInfo.newBuilder()
                        .build())
                .build());
    }

    @Test
    public void testPubGetList() {
        int fetchMessageLimit = 5;
        int fetchMessageOffset = 6;
        int queueIndex = 0;
        stub.publish(PublishRequest.newBuilder()
                .setTopicName(TOPIC)
                .setQueueIndex(queueIndex)
                .addAllMessages(messages)
                .build());

        Iterator itr = stub.getList(GetListRequest.newBuilder()
                .setTopicName(TOPIC)
                .setQueueIndex(queueIndex)
                .setOffset(fetchMessageOffset)
                .setLimit(fetchMessageLimit)
                .build());
        while (itr.hasNext()) {
            log.info("-> {}", itr.next());
        }
    }
    @Test
    public void testPubSub(){
        int queueIndex = 0;
        stub.publish(PublishRequest.newBuilder()
                .setTopicName(TOPIC)
                .setQueueIndex(queueIndex)
                .addAllMessages(messages)
                .build());

        Iterator itr= stub.subscribe(FetchRequest.newBuilder()
                        .setTopicName(TOPIC)
                        .setQueueIndex(queueIndex)
                        .setReplayPreset(ReplayPreset.EARLIEST)
                .build());
        while (itr.hasNext()) {
            log.info("test received: {}", itr.next());
        }
    }

    @AfterEach
    public void afterEach() {
        topicStub.deleteTopic(DeleteTopicRequest.newBuilder().setTopicName(TOPIC).build());
    }

}
