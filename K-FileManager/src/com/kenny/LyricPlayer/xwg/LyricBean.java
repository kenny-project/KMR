package com.kenny.LyricPlayer.xwg;

public class LyricBean
{
   private String _id;
   private String music_url;
   private String music_title;
   private String music_artist;
   private String music_duration;
   private String lyric_title;
   private String lyric_url;
   private String lyric_encoding;
   private String last_play;
   public String getId()
   {
      return _id;
   }
   public void setId(String id)
   {
      this._id = id;
   }
   public String getMusic_url()
   {
      return music_url;
   }
   public void setMusic_url(String music_url)
   {
      this.music_url = music_url;
   }
   public String getMusic_title()
   {
      return music_title;
   }
   public void setMusic_title(String music_title)
   {
      this.music_title = music_title.trim();
   }
   public String getMusic_artist()
   {
      return music_artist;
   }
   public void setMusic_artist(String music_artist)
   {
      this.music_artist = music_artist;
   }
   public String getMusic_duration()
   {
      return music_duration;
   }
   public void setMusic_duration(String music_duration)
   {
      this.music_duration = music_duration;
   }
   public String getLyric_title()
   {
      return lyric_title;
   }
   public void setLyric_title(String lyric_title)
   {
      this.lyric_title = lyric_title;
   }
   public String getLyric_url()
   {
      return lyric_url;
   }
   public void setLyric_url(String lyric_url)
   {
      this.lyric_url = lyric_url;
   }
   public String getLyric_encoding()
   {
      return lyric_encoding;
   }
   public void setLyric_encoding(String lyric_encoding)
   {
      this.lyric_encoding = lyric_encoding;
   }
   public String getLast_play()
   {
      return last_play;
   }
   public void setLast_play(String last_play)
   {
      this.last_play = last_play;
   }
}
