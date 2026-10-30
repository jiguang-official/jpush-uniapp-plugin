package uts.sdk.modules.jgJpushU;
import android.os.Looper;
import java.util.concurrent.atomic.AtomicInteger;
import kotlin.Unit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE,sdk=28)
@LooperMode(LooperMode.Mode.PAUSED)
public class UpgradeLifecycleTest {
 private PendingPushReply reply(long generation,AtomicInteger count) {return new PendingPushReply(generation,v->{count.incrementAndGet();return Unit.INSTANCE;},e->{count.incrementAndGet();return Unit.INSTANCE;});}
 @Test public void cleanupDeletesMultipleOldRepliesButPreservesNewGeneration() {
  var map=IndexKt.getPendingPushReplies();map.clear();var count=new AtomicInteger();long old=IndexKt.getUpgradeGeneration().get();
  map.set(101L,reply(old,count));map.set(102L,reply(old,count));
  IndexKt.clearUpgradeCallbacks();long current=IndexKt.getUpgradeGeneration().get();
  // A new-generation request enters before the queued cleanup task executes.
  map.set(103L,reply(current,count));Shadows.shadowOf(Looper.getMainLooper()).idle();
  assertEquals(1,map.size());assertTrue(map.has(103L));assertEquals(0,count.get());
  IndexKt.finishPushReply(101L,new PushServiceResult(0,null,null),null);
  IndexKt.finishPushReply(103L,new PushServiceResult(0,null,null),null);
  IndexKt.finishPushReply(103L,new PushServiceResult(0,null,null),null);
  Shadows.shadowOf(Looper.getMainLooper()).idle();assertEquals(1,count.get());assertTrue(map.isEmpty());
 }
 @Test public void queuedResultThenClearDoesNotCallDestroyedPage() {
  var map=IndexKt.getPendingPushReplies();map.clear();var count=new AtomicInteger();map.set(200L,reply(IndexKt.getUpgradeGeneration().get(),count));
  IndexKt.finishPushReply(200L,new PushServiceResult(0,null,null),null);IndexKt.clearUpgradeCallbacks();
  Shadows.shadowOf(Looper.getMainLooper()).idle();assertEquals(0,count.get());assertTrue(map.isEmpty());
 }
}
