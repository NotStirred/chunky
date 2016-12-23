package se.llbit.chunky.ui.render;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import se.llbit.json.JsonMember;
import se.llbit.json.JsonObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * This dialog lets the user export selected scene settings as a JSON string.
 *
 * <p>The dialog contains check boxes for selecting which parts of the
 * scene configuration should be exported. A text field is updated to
 * contain the JSON string for exporting the selected settings.
 */
class SettingsExport extends Stage {
  static final Set<String> excluded = new HashSet<>();
  static final Set<String> defaultIncluded = new HashSet<>();
  static final Map<String, Set<String>> groups = new LinkedHashMap<>();

  static {
    // Set up the hidden configuration variables.
    excluded.add("sdfVersion");
    excluded.add("name");

    // Render status variables should not be exported.
    excluded.add("renderTime");
    excluded.add("spp");
    excluded.add("pathTrace");

    groups.put("Camera", set("camera", "cameraPresets"));
    groups.put("Canvas size", set("width", "height"));
    groups.put("Emitters", set("emittersEnabled", "emitterIntensity"));
    groups.put("Entities", set("actors", "renderActors"));
    groups.put("Fog", set("fogColor", "fastFog", "fogDensity"));
    groups.put("Sky", set("sky", "transparentSky"));
    groups.put("Sun", set("sun", "sunEnabled"));
    groups.put("Water", set("waterColor", "waterOpacity", "waterVisibility", "useCustomWaterColor", "waterHeight", "stillWater"));
    groups.put("Misc", set("sppTarget", "dumpFrequency", "saveSnapshots", "world", "outputMode",
        "biomeColorsEnabled", "exposure"));
    groups.put("Advanced", set("postprocess", "rayDepth"));

    defaultIncluded.add("Fog");
    defaultIncluded.add("Water");
    defaultIncluded.add("Emitters");
    defaultIncluded.add("Sun");
    defaultIncluded.add("Sky");

    // Exclude the grouped options to avoid duplicates.
    for (Set<String> group : groups.values()) {
      for (String key : group) {
        excluded.add(key);
      }
    }
  }

  private static Set<String> set(String... members) {
    HashSet<String> set = new HashSet<>();
    if (members != null) {
      Collections.addAll(set, members);
    }
    return set;
  }

  private final JsonObject json;

  private Map<String, CheckBox> checkMap = new HashMap<>();
  private TextField jsonField = new TextField("{}");

  /**
   * @param json the complete scene settings JSON
   */
  SettingsExport(JsonObject json) {
    this.json = json;
    ScrollPane scrollPane = new ScrollPane();
    VBox vBox = new VBox();
    vBox.setPadding(new Insets(10));
    vBox.setSpacing(10);

    vBox.getChildren().add(new Label("Settings to export:"));

    for (Map.Entry<String, Set<String>> group : groups.entrySet()) {
      CheckBox checkBox = new CheckBox(group.getKey());
      checkBox.setSelected(defaultIncluded.contains(group.getKey()));
      checkBox.setOnAction(event -> update());
      vBox.getChildren().add(checkBox);
      for (String setting : group.getValue()) {
        checkMap.put(setting, checkBox);
      }
    }

    for (JsonMember setting : json.object().getMemberList()) {
      if (!excluded.contains(setting.getName())) {
        // TODO build a hierarchical checkbox system for complex settings.
        CheckBox checkBox = new CheckBox(setting.getName());
        checkBox.setSelected(defaultIncluded.contains(setting.getName()));
        checkBox.setOnAction(event -> update());
        vBox.getChildren().add(checkBox);
        checkMap.put(setting.getName(), checkBox);
      }
    }

    HBox exportBox = new HBox();
    exportBox.setAlignment(Pos.BASELINE_LEFT);
    exportBox.setSpacing(10);
    exportBox.getChildren().add(new Label("Settings JSON:"));
    exportBox.getChildren().add(jsonField);

    vBox.getChildren().add(exportBox);

    HBox buttonBox = new HBox();
    buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
    Button doneButton = new Button("Done");
    doneButton.setOnAction(event -> hide());
    buttonBox.getChildren().add(doneButton);

    vBox.getChildren().add(buttonBox);

    scrollPane.setContent(vBox);
    setScene(new Scene(scrollPane));
    setTitle("Settings Export");
    addEventFilter(KeyEvent.KEY_PRESSED, e -> {
      if (e.getCode() == KeyCode.ESCAPE) {
        e.consume();
        hide();
      }
    });

    update();

    jsonField.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        Platform.runLater(() -> jsonField.selectAll());
      }
    });

    setOnShowing(event -> {
      Platform.runLater(() -> jsonField.requestFocus());
    });
  }

  private void update() {
    JsonObject result = new JsonObject();
    for (JsonMember setting : json.object().getMemberList()) {
      CheckBox checkBox = checkMap.get(setting.getName());
      if (checkBox != null && checkBox.isSelected()) {
        result.addMember(setting.treeCopyNoTransform());
      }
    }
    jsonField.setText(result.toCompactString());
  }
}
