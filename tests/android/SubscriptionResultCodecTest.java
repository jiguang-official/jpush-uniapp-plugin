package uts.sdk.modules.jgJpushU;
import android.os.Bundle;
import org.json.JSONObject;
import org.json.JSONArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE,sdk=28)
public class SubscriptionResultCodecTest {
 @Test public void nullBundleHasEmptyExtraRatherThanThrowing() throws Exception {assertEquals(0,SubscriptionResultCodec.fromBundle(null).length());}
 @Test public void missingExtraStillPreservesCommandEnvelope() throws Exception {
  cn.jpush.android.api.CmdMessage command = new cn.jpush.android.api.CmdMessage(2013,104,"fixture",null);
  java.lang.reflect.Method method=PushMessageReceiver.class.getDeclaredMethod("convertCmdMessageToJson",cn.jpush.android.api.CmdMessage.class);method.setAccessible(true);
  JSONObject result=new JSONObject((String)method.invoke(new PushMessageReceiver(),command));assertEquals(2013,result.getInt("cmd"));assertEquals(104,result.getInt("errorCode"));assertEquals("fixture",result.getString("msg"));assertEquals(0,result.getJSONObject("extra").length());
 }
 @Test public void recursivePayloadRetainsNumbersBooleanArrayAndNull() throws Exception {
  Bundle child=new Bundle();child.putInt("code",104);child.putBoolean("success",false);child.putString("nullable",null);
  Bundle parent=new Bundle();parent.putBundle("nested",child);parent.putStringArray("channels",new String[]{"a","b"});parent.putString("intent_hidden","x");
  JSONObject encoded=SubscriptionResultCodec.fromBundle(parent);assertFalse(encoded.has("intent_hidden"));assertEquals(104,encoded.getJSONObject("nested").getInt("code"));assertFalse(encoded.getJSONObject("nested").getBoolean("success"));assertTrue(encoded.getJSONObject("nested").isNull("nullable"));assertEquals(2,encoded.getJSONArray("channels").length());
 }
}
