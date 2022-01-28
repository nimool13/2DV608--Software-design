package data;

import java.awt.event.ActionEvent;

public interface IBrick {
    void initialize();
    void actionPerformed(ActionEvent e);
    void setMosaicSizes();
    void updateInfo();
    void doColorMatch();
    void readColors();
    void exportBlXml();
    void generateDocs();


}
