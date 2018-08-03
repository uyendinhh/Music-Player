import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.Media;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import java.io.File;
import javafx.collections.MapChangeListener;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Modality;

/**
 * MusicPlayer class
 * @author udinh3
 * @version 1
 */
public class MusicPlayer extends Application {

    private TableView<Songs> tableView = new TableView<Songs>();
    private ObservableList<Songs> songProperty =
        FXCollections.observableArrayList();
    private TableColumn<Songs, String> fileName = new TableColumn("File Name");
    private TableColumn<Songs, String> attribute =
        new TableColumn("Attributes");
    private TableColumn<Songs, String> artist = new TableColumn("Artist");
    private TableColumn<Songs, String> title = new TableColumn("Title");
    private TableColumn<Songs, String> album = new TableColumn("Album");
    private File[] mediaList;
    private MediaPlayer mediaPlayer;
    private Button playBut, pauseBut, searchSongs, showAllSong;
    @Override
    public void start(Stage primaryStage) {

        //Set value to FileName, Artist, Title, Album
        tableView.setEditable(true);
        fileName.setMinWidth(350);
        fileName.setCellValueFactory(
            new PropertyValueFactory<Songs, String>("fileName"));
        artist.setMinWidth(200);
        artist.setCellValueFactory(
            new PropertyValueFactory<Songs, String>("artist"));
        title.setCellValueFactory(
            new PropertyValueFactory<Songs, String>("title"));
        title.setMinWidth(200);
        album.setCellValueFactory(
            new PropertyValueFactory<Songs, String>("album"));
        album.setMinWidth(200);

        tableView.setItems(songProperty);
        tableView.getColumns().addAll(fileName, attribute);
        attribute.getColumns().addAll(artist, title, album);

        //Create VBox which contains TableView and Buttons
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(5, 5, 5, 5));
        HBox buttonBar = createButton();
        vbox.getChildren().addAll(tableView, buttonBar);

        //Set action for playButton, pauseButton, searchSong, and showAllSongs
        playBut.setOnAction(e -> {
                if (playBut.getText().equals("Play")) {
                    mediaPlayer.play();
                    playBut.setDisable(true);
                    pauseBut.setDisable(false);
                }
            });

        pauseBut.setOnAction(e -> {
                if (pauseBut.getText().equals("Pause")) {
                    mediaPlayer.pause();
                    pauseBut.setDisable(true);
                    playBut.setDisable(false);
                }
            });

        searchSongs.setOnAction(e -> {
                if (searchSongs.getText().equals("Search Songs")) {
                    final Stage popup = new Stage();
                    popup.initModality(Modality.APPLICATION_MODAL);
                    VBox popVBox = new VBox();
                    popVBox.getChildren().add(new Button("Search"));
                    Scene popScene = new Scene(popVBox, 400, 200);
                    popup.setScene(popScene);
                    popup.show();
                    searchSongs.setDisable(true);
                    showAllSong.setDisable(false);
                }
            });

        showAllSong.setOnAction(e -> {
                if (showAllSong.getText().equals("Show all Songs")) {
                    tableView.setItems(songProperty);
                    showAllSong.setDisable(true);
                    searchSongs.setDisable(false);
                }
            });

        tableView.setOnMouseClicked(event -> {
                if (event.getClickCount() != 0) {
                    Songs playingSong = tableView.getSelectionModel().
                        getSelectedItem();
                    File fi = new File(playingSong.getFileName());
                    mediaPlayer = new MediaPlayer(
                        new Media(fi.toURI().toString()));
                }
            });

        //Show scene
        Scene scene = new Scene(vbox, 950, 400);
        primaryStage.setTitle("Music Player");
        primaryStage.setScene(scene);
        refreshTableView();
        primaryStage.show();
    }

    /**
     * createButton method containing Play, Pause,
     * Search Songs, and show all songs button
     * @return HBox
     */
    public HBox createButton() {
        HBox buttonBar = new HBox();
        playBut = new Button("Play");
        pauseBut = new Button("Pause");
        searchSongs = new Button("Search Songs");
        showAllSong = new Button("Show all Songs");
        buttonBar.setPadding(new Insets(5, 5, 5, 5));
        buttonBar.getChildren().addAll(playBut,
            pauseBut, searchSongs, showAllSong);
        return buttonBar;
    }

    /**
     * Songs class containing song's propeties
     */
    public static class Songs {
        private String fileName;
        private String artist;
        private String title;
        private String album;
        private Media media;

        //Songs constructor using File
        /**
         * Songs constructor
         * @param  file the music file being imported
         */
        public Songs(File file) {
            media = new Media(file.toURI().toString());
            media.getMetadata().addListener(
                new MapChangeListener<String, Object>() {
                    public void onChanged(Change<? extends String,
                        ? extends Object> mp3File) {
                        if (mp3File.wasAdded()) {
                            fileName = file.getName();
                            if (mp3File.getKey().toLowerCase().
                                equals("artist")) {
                                artist = mp3File.getValueAdded().toString();
                            }
                            if (mp3File.getKey().equals("album")) {
                                album = mp3File.getValueAdded().toString();
                            }
                            if (mp3File.getKey().toLowerCase().
                                equals("title")) {
                                title = mp3File.getValueAdded().toString();
                            }
                        }
                    }
                });
        }
        /**
         * getFileName getter method to get fileName
         * @return fileName String indicates the fileName
         */
        public String getFileName() {
            return fileName;
        }
        /**
         * getArtist getter method to get artist
         * @return artist String indicates the artist name
         */
        public String getArtist() {
            return artist;
        }
        /**
         * getTitle getter method to get title of the song
         * @return title String indicates the title of the song
         */
        public String getTitle() {
            return title;
        }
        /**
         * getAlbum getter method to get Album name
         * @return album
         */
        public String getAlbum() {
            return album;
        }
    }
    /**
     * refreshTableView method to refresh the TableView
     */
    void refreshTableView() {
        final ObservableList<TableColumn<Songs, ?>>
            songItem = tableView.getColumns();
        if (songItem != null) {
            final TableColumn fileNameCol = tableView.getColumns().get(0);
            songItem.remove(0);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    songItem.add(0, fileNameCol);
                }
            });
        }
    }

    @Override
    public void init() {
        File file = new File("").getAbsoluteFile();
        mediaList = file.listFiles();
        for (int i = 0; i < mediaList.length; i++) {
            if (mediaList[i].isFile() && mediaList[i].
                getName().contains(".mp3")) {
                songProperty.add(new Songs(mediaList[i]));
                Media media = new Media(
                    mediaList[i].toURI().toString());
            }
        }
    }
}