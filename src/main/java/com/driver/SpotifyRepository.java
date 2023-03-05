package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user = new User(name,mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        Artist artist = null;
        Album album = new Album(title); // new album object
        List<Album> newAlbumList = null; // new album list
        // If the artist exist, insert artist and their album list in the artistAlbum map
        boolean isArtistPresent = false;
        for(Artist artistObj : artists){
            String name = artistObj.getName();
            if(name.equals(artistName)){
                if(artistAlbumMap.containsKey(artistObj)){ // if artist exist in the artistAlbum map then get the list add album into that list
                    newAlbumList = artistAlbumMap.get(artistObj);
                    newAlbumList.add(album);
                    artistAlbumMap.put(artistObj,newAlbumList); // used - 1st time
                }else {
                    newAlbumList = new ArrayList<>();
                    newAlbumList.add(album);
                    artistAlbumMap.put(artistObj,newAlbumList);
                }
                isArtistPresent = true;
            }
        }
        // If the artist does not exist, the API creates a new artist first.
        if(!isArtistPresent){
            newAlbumList = new ArrayList<>();
            newAlbumList.add(album);
            artist = createArtist(artistName);
            artistAlbumMap.put(artist,newAlbumList); // used - 2nd time
        }
        albums.add(album); // update our main album list
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        //  album song map may be use, key - album, value - List<Song>
        Song song = null;
        List<Song> newSong = null;
        boolean isAlbumExist = false;
        for (Album album : albums){
            String name = album.getTitle();
            if(name.equals(albumName)){
                song  = new Song(title,length);
                if(albumSongMap.containsKey(album)){
                    newSong = albumSongMap.get(album); // get exist song list
                    newSong.add(song); // add song into existing list
                    albumSongMap.put(album,newSong);
                }else {
                    newSong = new ArrayList<>(); // song list not exist in hashmap so create new one
                    newSong.add(song); // add song into new list
                    albumSongMap.put(album,newSong); // insert new album in albumSong map
                }
                songs.add(song); // update main song list
                isAlbumExist = true;
            }
        }
        if(!isAlbumExist){
            throw new Exception("Album does not exist");
        }
        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        // HashMap<Playlist, List<Song>> playlistSongMap;
        Playlist playlist = null;
        List<Song> listOfSongs = null;
        List<User> listOfUsers = new ArrayList<>();
        List<Playlist> userPlaylist;
        boolean isUserExist = false;
        for(User user : users){ // get the current user from users list
            String number = user.getMobile();
            if(number.equals(mobile)){
                listOfSongs = new ArrayList<>();
                playlist = new Playlist(title);
                userPlaylistMap.put(user,userPlaylistMap.getOrDefault(user,new ArrayList<>()));
                userPlaylist = userPlaylistMap.get(user);
                if(!userPlaylist.contains(playlist)){
                    userPlaylist.add(playlist);
                    userPlaylistMap.put(user,userPlaylist);
                }
                listOfUsers.add(user);
                for(Song song : songs){ // find the song which length is equal to the current songs length
                    int duration = song.getLength();
                    if(duration == length){
                        listOfSongs.add(song); // valid songs are added to the list
                    }
                }
                playlistSongMap.put(playlist,listOfSongs); // insert playlist and listOfSong in the playListSongMap
                creatorPlaylistMap.put(user,playlist); // user and play list mapping
                playlistListenerMap.put(playlist,listOfUsers);
                playlists.add(playlist); // update main playlists list
                isUserExist = true;
            }
        }
        if(!isUserExist){
            throw new Exception("User does not exist");
        }
        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        Playlist playlist = null;
        List<Song> listOfSongs;
        List<User> listOfUsers = new ArrayList<>();
        List<Playlist> userPlaylist;
        boolean isUserExist = false;
        for(User user : users){ // get the current user from users list
            String number = user.getMobile();
            if(number.equals(mobile)){
                listOfSongs = new ArrayList<>();
                playlist = new Playlist(title);
                userPlaylistMap.put(user,userPlaylistMap.getOrDefault(user,new ArrayList<>()));
                userPlaylist = userPlaylistMap.get(user);
                if(!userPlaylist.contains(playlist)){
                    userPlaylist.add(playlist);
                    userPlaylistMap.put(user,userPlaylist);
                }
                listOfUsers.add(user);
                for(Song song : songs){
                    String name = song.getTitle();
                    if(songTitles.contains(name)){ // find the song which is present in the songTitles list
                        listOfSongs.add(song); // valid songs are added to the list
                    }
                }
                playlistSongMap.put(playlist,listOfSongs); // insert playlist and listOfSong in the playListSongMap
                creatorPlaylistMap.put(user,playlist); // user and play list mapping
                playlistListenerMap.put(playlist,listOfUsers);
                playlists.add(playlist); // update main playlists list
                isUserExist = true;
            }
        }
        if(!isUserExist){
            throw new Exception("User does not exist");
        }
        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        Playlist playlist = null;
        boolean isUserExist = false;
        boolean isPlayListExist = false;
        for(User user : users){
            String number = user.getMobile();
            if(number.equals(mobile)){
                for(Playlist playlistObj : playlists){
                    String name = playlistObj.getTitle();
                    if(name.equals(playlistTitle)){
                        playlist = playlistObj;
                        if(playlistListenerMap.containsKey(playlistObj)){
                            List<User> userList = playlistListenerMap.get(playlistObj);
                            if(!userList.contains(user)){ //If the user is not creator or not a listener, do nothing
                                userList.add(user);
                                playlistListenerMap.put(playlist,userList); //update playlist listener map
                            }
                        }

                        isPlayListExist = true;
                    }
                }
                isUserExist = true;
            }
        }
        if(!isUserExist){ //If the user does not exist, throw "User does not exist" exception
            throw new Exception("User does not exist");
        } else if (!isPlayListExist) { //If the playlist does not exist, throw "Playlist does not exist" exception
            throw new Exception("Playlist does not exist");
        }
        return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        return  new Song();
    }

    public String mostPopularArtist() {
        return "";
    }

    public String mostPopularSong() {
        return "";
    }
}
