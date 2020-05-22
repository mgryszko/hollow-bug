package hollow

import com.netflix.hollow.api.codegen.HollowAPIGenerator
import com.netflix.hollow.api.consumer.HollowConsumer
import com.netflix.hollow.api.consumer.fs.HollowFilesystemAnnouncementWatcher
import com.netflix.hollow.api.consumer.fs.HollowFilesystemBlobRetriever
import com.netflix.hollow.api.producer.HollowProducer
import com.netflix.hollow.api.producer.fs.HollowFilesystemAnnouncer
import com.netflix.hollow.api.producer.fs.HollowFilesystemPublisher
import com.netflix.hollow.core.write.HollowWriteStateEngine
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper
import hollow.api.MovieAPI
import java.nio.file.Path
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals


class HollowTest {
    val path: Path = createTempDir().toPath()
    val publisher = HollowFilesystemPublisher(path)
    val announcer = HollowFilesystemAnnouncer(path)
    val producer: HollowProducer = HollowProducer
        .withPublisher(publisher)
        .withAnnouncer(announcer)
        .build()
    val retriever: HollowConsumer.BlobRetriever = HollowFilesystemBlobRetriever(path)
    val announcementWatcher: HollowConsumer.AnnouncementWatcher = HollowFilesystemAnnouncementWatcher(path)
    val consumer: HollowConsumer = HollowConsumerBuilder.newHollowConsumer()
        .withBlobRetriever(retriever)
        .withAnnouncementWatcher(announcementWatcher)
        .withGeneratedAPIClass(MovieAPI::class.java)
        .build()

    val moviesToStore: List<Movie> = listOf(
        Movie(1, "The Matrix", 1999),
        Movie(2, "Beasts of No Nation", 2015),
        Movie(3, "Pulp Fiction", 1994)
    )

    @Test
    fun `produce and consume`() {
        producer.runCycle { state -> moviesToStore.forEach { state.add(it) } }

        consumer.triggerRefresh()

        val movieAPI = consumer.getAPI(MovieAPI::class.java)
        val retrievedMovies = movieAPI.allMovie.map { Movie(it.id, it.title.value, it.releaseYear) }

        assertEquals(moviesToStore, retrievedMovies)
    }

    @Test
    @Ignore
    fun `generate source files`() {
        val writeEngine = HollowWriteStateEngine()
        val mapper = HollowObjectMapper(writeEngine)
        mapper.initializeTypeState(Movie::class.java)

        val generator = HollowAPIGenerator.Builder().withAPIClassname("MovieAPI")
            .withPackageName("hollow.api")
            .withDataModel(writeEngine)
            .withDestination("./src/test/java")
            .build()

        generator.generateSourceFiles()
    }
}