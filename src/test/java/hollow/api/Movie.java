package hollow.api;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class Movie extends HollowObject {

    public Movie(MovieDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long getId() {
        return delegate().getId(ordinal);
    }

    public Long getIdBoxed() {
        return delegate().getIdBoxed(ordinal);
    }

    public HString getTitle() {
        int refOrdinal = delegate().getTitleOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public int getReleaseYear() {
        return delegate().getReleaseYear(ordinal);
    }

    public Integer getReleaseYearBoxed() {
        return delegate().getReleaseYearBoxed(ordinal);
    }

    public MovieAPI api() {
        return typeApi().getAPI();
    }

    public MovieTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected MovieDelegate delegate() {
        return (MovieDelegate)delegate;
    }

}