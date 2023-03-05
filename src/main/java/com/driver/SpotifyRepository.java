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
        User user = null;
        for(User currUser : users){
            if(currUser.getMobile().equals(mobile))user = currUser;
        }
        // user does not exist
        if(user == null)throw new Exception("User does not exist");

        Playlist playlist = null;
        for(Playlist currPlaylist: playlists){
            if(currPlaylist.getTitle().equals(playlistTitle))playlist = currPlaylist;
        }
        // playlist does not exist
        if(playlist == null)throw new Exception("Playlist does not exist");

        // if user is creator of playlist do nothing and return playlist
        if(creatorPlaylistMap.containsKey(user)){
            if(creatorPlaylistMap.get(user).getTitle().equals(playlistTitle))return playlist;
        }
        // if user is already present in listener list do nothing and return playlist
        List<User> listenerList = new ArrayList<>();
        if(playlistListenerMap.containsKey(playlist)){
            listenerList = playlistListenerMap.get(playlist);
            for(User user1 : listenerList){
                if(user1.getMobile().equals(mobile)) return playlist;
            }
        }
        // update listener map
        listenerList.add(user);
        playlistListenerMap.put(playlist,listenerList);

//        Playlist playlist = null;
//        boolean isUserExist = false;
//        boolean isPlayListExist = false;
//        for(User user : users){
//            String number = user.getMobile();
//            if(number.equals(mobile)){
//                for(Playlist playlistObj : playlists){
//                    String name = playlistObj.getTitle();
//                    if(name.equals(playlistTitle)){
//                        playlist = playlistObj;
//
//                        if(playlistListenerMap.containsKey(playlistObj)){
//                            List<User> userList = playlistListenerMap.get(playlistObj);
//                            if(!userList.contains(user)){ //If the user is not creator or not a listener, do nothing
//                                userList.add(user);
//                                playlistListenerMap.put(playlist,userList); //update playlist listener map
//                            }
//                        }
//                        isPlayListExist = true;
//                    }
//                }
//                isUserExist = true;
//            }
//        }
//        if(!isUserExist){ //If the user does not exist, throw "User does not exist" exception
//            throw new Exception("User does not exist");
//        } else if (!isPlayListExist) { //If the playlist does not exist, throw "Playlist does not exist" exception
//            throw new Exception("Playlist does not exist");
//        }
        return playlist;
    }
    public Song likeSong(String mobile, String songTitle) throws Exception {
        //The user likes the given song. The corresponding artist of the song gets auto-liked
        //A song can be liked by a user only once. If a user tried to like a song multiple times, do nothing
        //However, an artist can indirectly have multiple likes from a user, if the user has liked multiple songs of that artist.
        //If the user does not exist, throw "User does not exist" exception
        //If the song does not exist, throw "Song does not exist" exception
        //Return the song after updating
        User user = null;
        for(User currUser : users){
            if(currUser.getMobile().equals(mobile))user = currUser;
        }
        // user does not exist
        if(user == null)throw new Exception("User does not exist");

        Song song = null;
        for(Song currSong: songs){
            if(currSong.getTitle().equals(songTitle))song = currSong;
        }
        // playlist does not exist
        if(song == null)throw new Exception("Song does not exist");
        // if user has already liked song
        List<User> songLikeUser = new ArrayList<>();
        if(songLikeMap.containsKey(song)) songLikeUser = songLikeMap.get(song);
        for(User userLike : songLikeUser){
            if(userLike.getMobile().equals(mobile)) return song;
        }

        // update song like
        song.setLikes(song.getLikes()+1);
        songLikeUser.add(user);
        songLikeMap.put(song,songLikeUser);

        // update like of artist
        updateArtistLike(song);

        return song;

    }
    public String mostPopularArtist() {
        int like = -1;
        String popularArtist = "";
        for (Artist artist: artists){
            if(like < artist.getLikes()){
                like = artist.getLikes();
                popularArtist = artist.getName();
            }
        }
        return popularArtist;
    }
    public String mostPopularSong() {
        int like = -1;
        String popularSong = "";
        for (Song song: songs){
            if(like < song.getLikes()){
                like = song.getLikes();
                popularSong = song.getTitle();
            }
        }
        return popularSong;
    }
    public void updateArtistLike(Song likedSong){
        int like = -1;
        String popularArtist = "";
        for(Artist artist: artists){
            if(artistAlbumMap.containsKey(artist)){
                List<Album> albumList= artistAlbumMap.get(artist);
                for (Album album : albumList){
                    if(albumSongMap.containsKey(album)){
                        List<Song> songList = albumSongMap.get(album);
                        for (Song song : songList){
                            if(song.equals(likedSong)){
                                artist.setLikes(artist.getLikes()+1);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
}
