package com.keti.tas.alljoyn;

import java.util.Map;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusSignal;
import org.alljoyn.bus.annotation.Position;

/***
 * 
 * @author ChenNan
 * @version 0.0.0.1
 */
@BusInterface(name = "net.allplay.MediaPlayer")
public interface MusaicPlay10Interface {

	public class PlayerInfo {
		@Position(0)
		public String displayName;
		@Position(1)
		public String[] capabilities;
		@Position(2)
		public int maximumVolume;
		@Position(3)
		public ZoneInfo zoneInfo;
	}
	
	public class ZoneInfo {
		@Position(0)
		public String name;
		@Position(1)
		public int number;
		@Position(2)
		public Variant value;
	}
	
	public class PlayItem {
		@Position(0)
		public String value1;
		@Position(1)
		public String value2;
		@Position(2)
		public String value3;
		@Position(3)
		public String value4;
		@Position(4)
		public long value5;
		@Position(5)
		public String value6;
		@Position(6)
		public String value7;
		@Position(7)
		public String value8;
		@Position(8)
		public Map<String, String> value9;
		@Position(9)
		public Map<String, Variant> value10;
		@Position(10)
		public Variant value11;
	}
	
	public class PlayList {
		@Position(0)
		public PlayItem[] items;
		@Position(1)
		public String controllerType;
		@Position(2)
		public String playlistUserData;
	}
	
	public class PlaylistInfo {
		@Position(0)
		public String controllerType;
		@Position(1)
		public String playlistUserData;
	}
	
	public class PlaybackError {
		@Position(0)
		public int index;
		@Position(1)
		public String error;
		@Position(2)
		public String description;
	}
	
	public class PlayState {
		@Position(0)
		public String value1;
		@Position(1)
		public long value2;
		@Position(2)
		public int value3;
		@Position(3)
		public int value4;
		@Position(4)
		public int value5;
		@Position(5)
		public int value6;
		@Position(6)
		public int value7;
		@Position(7)
		public PlayItem[] value8;
	}
	
	public static final short VERSION = 1;
	
	@BusSignal(name="EnabledControlsChanged", signature="a{sb}")
	public void EnabledControlsChanged(Map<String, Boolean> value) throws BusException; 
	
	@BusSignal(name="EndOfPlayback")
	public void EndOfPlayback() throws BusException;
	
	@BusMethod(name="ForcedPrevious")
	public void ForcedPrevious() throws BusException;
	
	@BusMethod(name="GetPlayerInfo", replySignature="sasi(siv)")
	public PlayerInfo GetPlayerInfo() throws BusException;
	
	@BusMethod(name="GetPlaylist", replySignature="a(ssssxsssa{ss}a{sv}v)ss")
	public PlayList GetPlaylist() throws BusException;
	
	@BusMethod(name="GetPlaylistInfo", replySignature="ss")
	public PlaylistInfo GetPlaylistInfo() throws BusException;
	
	@BusSignal(name="InterruptibleChanged", signature="b")
	public void InterruptibleChanged(boolean value) throws BusException;
	
	@BusSignal(name="LoopModeChanged", signature="s")
	public void LoopModeChanged(String value) throws BusException;
	
	@BusMethod(name="Next")
	public void Next() throws BusException;
	
	@BusSignal(name="OnPlaybackError", replySignature="iss")
	public void OnPlaybackError(PlaybackError value) throws BusException;
	
	@BusMethod(name="Pause")
	public void Pause() throws BusException;
	
	@BusMethod(name="Play", signature="ixb")
	public void Play(int itemIndex, long startPositionMsecs, boolean pauseStateOnly) throws BusException;
	
	@BusMethod(name="PlayItem", signature="ssssxsssa{ss}a{sv}v")
	public void PalyItem(PlayItem item) throws BusException;
	
	@BusSignal(name="PlayStateChanged", signature="sxuuuiia(ssssxsssa{ss}a{sv}v)")
	public void PlayStateChanged(PlayState value) throws BusException;
	
	@BusSignal(name="PlaylistChanged")
	public void PlaylistChanged() throws BusException;
	
	@BusMethod(name="Previous")
	public void Previous() throws BusException;
	
	@BusMethod(name="Resume")
	public void Resume() throws BusException;
	
	@BusMethod(name="SetPosition", signature="x")
	public void SetPosition(long x) throws BusException;
	
	@BusSignal(name="ShuffleModeChanged", signature="s")
	public void ShuffleModeChanged(String value) throws BusException;
	
	@BusMethod(name="Stop")
	public void Stop() throws BusException;
	
	@BusMethod(name="UpdatePlaylist", signature="a(ssssxsssa{ss}a{sv}v)iss")
	public void UpdatePlaylist(PlayItem[] playlistItems, int index, String controllerType, String playlistUserData) throws BusException;
	
	@BusProperty(name="EnabledControls", signature="a{sb}")
	public Map<String, Boolean> getEnabledControls() throws BusException;
	
	@BusProperty(name="Interruptible", signature="b")
	public boolean getInterruptible() throws BusException;
	
	@BusProperty(name="LoopMode", signature="s")
	public String getLoopMode() throws BusException;
	
	@BusProperty(name="LoopMode", signature="s")
	public void setLoopMode(String value) throws BusException;
	
	@BusProperty(name="PlayState", signature="sxuuuiia(ssssxsssa{ss}a{sv}v)")
	public PlayState getPlayState() throws BusException;
	
	@BusProperty(name="ShuffleMode", signature="s")
	public String getShuffleMode() throws BusException;
	
	@BusProperty(name="ShuffleMode", signature="s")
	public void setShuffleMode(String value) throws BusException;
	
	@BusProperty(name="Version", signature="q")
	public short getVersion() throws BusException;
	
}
