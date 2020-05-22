package hollow;

import com.netflix.hollow.api.consumer.HollowConsumer;

// workaround for https://github.com/Netflix/hollow/issues/252
public final class HollowConsumerBuilder
{
  private HollowConsumerBuilder()
  {
  }

  @SuppressWarnings("rawtypes")
  public static HollowConsumer.Builder newHollowConsumer()
  {
    return new HollowConsumer.Builder();
  }
}
