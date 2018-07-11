package sample;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;


public class Controller {
    @FXML
    private Button btnChoseDirectory;
    @FXML
    private Button btnJsonFile;
    @FXML
    private Button btnSortImages;
    @FXML
    private Label labelPath;
    @FXML
    private Label labelOutputPath;
    @FXML
    private Label labelPathToJSON;
    @FXML
    private Label labelInfo;

    @FXML
    private Label labelTotal;
    @FXML
    private Label labelSuccessfully;
    @FXML
    private Label labelErrors;

    private int totalToSort = 0;
    private int totalSuccessed = 0;
    private int totalWarrings = 0;

    File selectedDirectory = null; // folder like idetities..
    File outputFilePath = null; // folder to output
    File jsonfile = null; // path to json file

    String folderNameMultiple = "Multiple";
    String folderNameTrashFolder = "TrashFolder";

    String folderNameNotLgOrCenter = "NotCenter";
    String folderNameExtra = "Extra";
    String folderNameTrash = "Trash";

    String folderNameNO_THUMBNAILS = "NO_THUMBNAILS";
    String folderNameINVALID_FOLDER_CHECKED = "INVALID_FOLDER_CHECKED";

     ArrayList arrMultipleFolders = null;
     ArrayList arrTrashFolderFolders = null;

     ArrayList arrNotLgOrCenterImages = null;
     ArrayList arrExtraImages = null;
     ArrayList arrTrashImages = null;

    ArrayList arrNO_THUMBNAILS = null;
    ArrayList arrINVALID_FOLDER_CHECKED = null;

    Logger logger = Logger.getLogger("MyLog");
    FileHandler fh;


    @FXML
    public void initialize(){
        btnChoseDirectory.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
            @Override
            public void handle (MouseEvent mouseEvent){
                DirectoryChooser chooser = new DirectoryChooser();
                chooser.setTitle("Folder that contain (example) <identities_0> and etc ");
/*                File defaultDirectory = new File("c:/");
                chooser.setInitialDirectory(defaultDirectory);*/
                selectedDirectory = chooser.showDialog(new Stage());
                if(selectedDirectory!=null) labelPath.setText(selectedDirectory.getAbsolutePath());

                outputFilePath = new File(selectedDirectory.getAbsolutePath() + "_bad");
                if(outputFilePath.exists()==false) outputFilePath.mkdirs();
                if(outputFilePath!=null) labelOutputPath.setText(outputFilePath.getAbsolutePath());
            }
        });

        btnJsonFile.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
            @Override
            public void handle (MouseEvent mouseEvent){
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Json File");
                //Set extension filter
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("json files (*.json)", "*.json");
                fileChooser.getExtensionFilters().add(extFilter);

                //Show save file dialog
                jsonfile = fileChooser.showOpenDialog(new Stage());

                if(jsonfile != null){
                    labelPathToJSON.setText(jsonfile.getAbsolutePath());
                }
            }
        });

        btnSortImages.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
            @Override
            public void handle (MouseEvent mouseEvent){
               if(selectedDirectory==null){
                   labelInfo.setTextFill(Color.RED);
                   labelInfo.setText("You should select images directory.");return;
               }
               else if(outputFilePath==null){
                   labelInfo.setTextFill(Color.RED);
                   labelInfo.setText("You should select output directory.");return;
               }
               else if(jsonfile==null){
                   labelInfo.setTextFill(Color.RED);
                   labelInfo.setText("You should select json file.");return;
               }

                disableAllButtons();
                labelInfo.setTextFill(Color.BLUE);
                labelInfo.setText("Processing..");
                sorting();
            }
        });
    }

    private void disableAllButtons(){
        btnSortImages.setDisable(true);
        btnChoseDirectory.setDisable(true);
        btnJsonFile.setDisable(true);
    }

    private void activateAllButtons(){
        btnSortImages.setDisable(false);
        btnChoseDirectory.setDisable(false);
        btnJsonFile.setDisable(false);
    }

    private void sorting() {
        try {

            // This block configure the logger with handler and formatter
            fh = new FileHandler(selectedDirectory.getAbsolutePath() + ".log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Selected dir : " + selectedDirectory.getAbsolutePath());
        logger.info("OutPut dir : " + outputFilePath.getAbsolutePath());
        logger.info("Json File " + jsonfile.getAbsolutePath());


        JSONParser parser = new JSONParser();

        try {

            Object obj = parser.parse(new FileReader(jsonfile.getAbsolutePath()));

            JSONObject jsonObject = (JSONObject) obj;


            arrMultipleFolders = new ArrayList();
            JSONArray msg = (JSONArray) jsonObject.get("Multiple");
            Iterator<String> iterator = msg.iterator();
            while (iterator.hasNext()) {
                arrMultipleFolders.add(iterator.next());
            }

            arrTrashFolderFolders = new ArrayList();
            msg = (JSONArray) jsonObject.get("TrashFolder");
            iterator = msg.iterator();
            while (iterator.hasNext()) {
                arrTrashFolderFolders.add(iterator.next());
            }

            arrNotLgOrCenterImages = new ArrayList();
            msg = (JSONArray) jsonObject.get("NotLgOrCenter");
            iterator = msg.iterator();
            while (iterator.hasNext()) {
                arrNotLgOrCenterImages.add(iterator.next());
            }

            arrExtraImages = new ArrayList();
            msg = (JSONArray) jsonObject.get("Extra");
            iterator = msg.iterator();
            while (iterator.hasNext()) {
                arrExtraImages.add(iterator.next());
            }

            arrTrashImages = new ArrayList();
            msg = (JSONArray) jsonObject.get("TrashImages");
            iterator = msg.iterator();
            while (iterator.hasNext()) {
                arrTrashImages.add(iterator.next());
            }
            // error arrays
            arrNO_THUMBNAILS = new ArrayList();
            msg = (JSONArray) jsonObject.get("NO_THUMBNAILS");
            iterator = msg.iterator();
            while (iterator.hasNext()) {
                arrNO_THUMBNAILS.add(iterator.next());
            }

            arrINVALID_FOLDER_CHECKED = new ArrayList();
            msg = (JSONArray) jsonObject.get("INVALID_FOLDER_CHECKED");
            iterator = msg.iterator();
            while (iterator.hasNext()) {
                arrINVALID_FOLDER_CHECKED.add(iterator.next());
            }

        } catch (FileNotFoundException e) {
            logger.warning(e.toString());
        } catch (IOException e) {
            logger.warning(e.toString());
        } catch (ParseException e) {
            logger.warning(e.toString());
        }
        // create bad images directories
        resetStatsLabels();


        totalToSort = arrExtraImages.size() + arrTrashImages.size() + arrNotLgOrCenterImages.size() + arrMultipleFolders.size() + arrTrashFolderFolders.size() +arrINVALID_FOLDER_CHECKED.size() + arrNO_THUMBNAILS.size();
        labelTotal.setText("Total : " +String.valueOf(totalToSort));

        sortImages();
        renameGoodFolders();
        stopProcessing("All done!",true);
    }

    private void sortImages(){
        // sort images
        if(sortImagesStep(arrNotLgOrCenterImages,folderNameNotLgOrCenter,true)) logger.info("Done NotLgOrCenterImages");
        else logger.info("Sort return false NotLgOrCenterImages (maybe records is empty) ");
        if(sortImagesStep(arrExtraImages,folderNameExtra,true)) logger.info("Done ExtraImages");
        else logger.info("Sort return false ExtraImages (maybe records is empty) ");
        if(sortImagesStep(arrTrashImages,folderNameTrash,true)) logger.info("Done TrashImages");
        else logger.info("Sort return false TrashImages (maybe records is empty) ");
        // sort folders
        if( sortImagesStep(arrMultipleFolders,folderNameMultiple,false)) logger.info("Done MultipleFolders");
        else logger.info("Sort return false MultipleFolders (maybe records is empty) ");
        if(sortImagesStep(arrTrashFolderFolders,folderNameTrashFolder,false)) logger.info("Done TrashFolderFolders");
        else logger.info("Sort return false TrashFolderFolders (maybe records is empty) ");
        // sort errors folders
        if( sortImagesStep(arrNO_THUMBNAILS,folderNameNO_THUMBNAILS,false)) logger.info("Done NO_THUMBNAILS");
        else logger.info("Sort return false NO_THUMBNAILS (maybe records is empty) ");
        if( sortImagesStep(arrINVALID_FOLDER_CHECKED,folderNameINVALID_FOLDER_CHECKED,false)) logger.info("Done INVALID_FOLDER_CHECKED");
        else logger.info("Sort return false INVALID_FOLDER_CHECKED (maybe records is empty) ");
    }

    private boolean sortImagesStep(ArrayList arrImages,String folderName,boolean isImages){
        File tmpFile = null;

        File tmpFolder = null;
        if (arrImages.isEmpty()) return false;
        for (Object imagepath:arrImages) {

            tmpFile= new File(selectedDirectory.getAbsolutePath() + File.separator + imagepath.toString());
            String firstSymbol = String.valueOf(imagepath.toString().charAt(0));

            tmpFolder = new File(outputFilePath.getAbsolutePath()+File.separator+ firstSymbol + File.separator + folderName);
            if(tmpFolder.exists()==false) tmpFolder.mkdirs();

            if(isImages) {
                tmpFolder = new File(tmpFile.getParent());
                tmpFolder = new File(outputFilePath.getAbsolutePath() + File.separator + firstSymbol + File.separator + folderName + File.separator + tmpFolder.getName());
                if (tmpFolder.exists() == false) tmpFolder.mkdirs();
            }

            if(tmpFile.exists()) {
                tmpFile.renameTo(new File(outputFilePath.getAbsolutePath()+File.separator+ firstSymbol + File.separator + folderName + File.separator +imagepath.toString() ));
                addTotalSuccesLabel();
            }
            else {
                logger.warning("file isnt exists" + tmpFile.getAbsolutePath());
                addTotalWarringsLabel();
            }
        }
        return true;
    }

    private void stopProcessing(String label,boolean success){
       labelInfo.setText(label);

       activateAllButtons();
       if(success==true){
           labelInfo.setTextFill(Color.GREEN);
           labelPath.setText("Path:");              selectedDirectory = null;
           labelOutputPath.setText("Output Path:"); outputFilePath = null;
           labelPathToJSON.setText("Json:");        jsonfile = null;
       }
       else{
           labelInfo.setTextFill(Color.RED);
       }
    }

    private void renameGoodFolders(){
        File tmpFile = null;
        File tmpFolder = null;

        List<File> listOfDirectories = Arrays.asList(selectedDirectory.listFiles());
        for(Object dirPath:listOfDirectories){

            tmpFile= new File(dirPath.toString()); // сама папка
            String firstSymbol = String.valueOf(tmpFile.getName().charAt(0)); // подпапка куда нужно вложить

            tmpFolder = new File(tmpFile.getAbsolutePath());
            tmpFolder = new File(tmpFolder.getParent()+File.separator+firstSymbol);

            if(tmpFolder.exists()==false) tmpFolder.mkdirs();
            if(tmpFile.exists()) tmpFile.renameTo(new File(tmpFolder.getAbsolutePath()+File.separator+ tmpFile.getName() ));


        }
    }

    private void resetStatsLabels(){
        totalToSort=0;
        totalSuccessed=0;
        totalWarrings=0;
        labelErrors.setText("Warring's : 0");
        labelSuccessfully.setText("Successfuly : 0");
        labelTotal.setText("Total : 0");
    }

    private void addTotalSuccesLabel(){
        totalSuccessed+=1;
        labelSuccessfully.setText("Successfuly : " + totalSuccessed);
    }

    private void addTotalWarringsLabel(){
        totalWarrings+=1;
        labelErrors.setText("Warring's : " + totalWarrings);
    }
}


