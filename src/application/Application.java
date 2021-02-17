/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import application.form.FormExport;
import application.form.FormLoad;
import application.form.FormMonster;
import application.form.FormProperty;
import application.form.FormSave;
import entities.Map;
import entities.Monster;
import entities.Property;
import entities.Tile;
import entities.Zone;
import generator.BSPDungeon;
import graphics.DrawableArea;
import graphics.DrawableCell;
import graphics.DrawableGrid;
import graphics.DrawableObject;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JLabel;
import utility.Animation;

/**
 *
 * @author 2014130020
 */
public class Application extends javax.swing.JFrame {

    private FormMonster formMonster;
    private FormProperty formProperty;
    private FormSave formSave;
    private FormLoad formLoad;
    private FormExport formExport;

    public Application() {
        initComponents();

        Database database = Database.getInstance();
        database.init();

        Assets assets = Assets.getInstance();
        assets.load();

        initAnimation(60, lbStatFPS, lbInfo);

        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
                initGenerator();
                initTileEditor();
                initDNDSupport();
            }
        });
    }

    private Timer timer;

    private void initAnimation(int defaultFPS, final JLabel statFPS, final JLabel infoMap) {

        final Map map = Database.getInstance().getMap();

        // create an animation timer
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            // time resolution
            long prevTime, timeDiff;

            @Override
            public void run() {
                // redraw
                repaint();

                timeDiff = System.currentTimeMillis() - prevTime;
                showFPS();
                showInfo();
                prevTime = System.currentTimeMillis();
            }

            int currentFPS, frameCount;
            long tick;

            private void showFPS() {
                if ((tick += timeDiff) < 1000L) {
                    frameCount++;
                } else {
                    currentFPS = frameCount;
                    // reset fps variable
                    tick = 0;
                    frameCount = 0;
                }
                statFPS.setText("FPS_" + currentFPS);
            }

            private void showInfo() {
                infoMap.setText("MAP_" + map.getMapId());
            }

        }, 0, 1000 / defaultFPS);
    }

    private ArrayList<DrawableObject[]> list;
    private int choice = -1;

    private void initGenerator() {
        if (choice == -1) {
            list = new ArrayList<>();
            // adding wall to list
            list.add(imageToObject(Assets.getInstance().get("brick-wall")));
            list.add(imageToObject(Assets.getInstance().get("green-stone-wall")));
            list.add(imageToObject(Assets.getInstance().get("rock-wall")));
            list.add(imageToObject(Assets.getInstance().get("stone-wall")));

            choice = 0;
        }

        final DrawableObject[] selectedWall = list.get(choice);

        DrawableGrid grid = panelWalls.getGrid();
        grid.getCell(0, 0).setUserObject(selectedWall[0]);
        grid.getCell(1, 0).setUserObject(selectedWall[1]);
    }

    private void initTileEditor() {
        initTileMonsters();
        initTileProperties();
        initTileFloors();
    }

    private void initTileMonsters() {

        DrawableGrid grid = panelMonsters.getGrid();
        Database database = Database.getInstance();

        List<Monster> monsters = database.getMonsters();

        Iterator<Monster> iterator = monsters.iterator();
        // reordering grid cell row to column
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                if (iterator.hasNext()) {

                    final Monster monster = iterator.next();

                    grid.getCell(x, y).setUserObject(new DrawableObject(monster.getMonsterId()) {

                        Animation animation = null;

                        @Override
                        public void draw(Graphics2D g, int x, int y, int width, int height) {
                            if (animation == null) {
                                animation = new Animation();
                                animation.setAnimation(Assets.getInstance().get(monster.getFile()), true);
                                animation.setDelay(10);
                            }

                            animation.updateAnimation();
                            g.drawImage(animation.getCurrentImage(), x, y, width, height, null);

                            String rarity = monster.getRarity();

                            if (rarity.equals("Rare") || rarity.equals("Epic") || rarity.equals("Legend")) {
                                Stroke old = g.getStroke();

                                g.setPaint(Color.RED);
                                g.setStroke(new BasicStroke(2.0f, 0, 0));
                                g.draw(new Rectangle2D.Float(x, y, width, height));

                                g.setStroke(old);
                            }
                        }
                    });

                } else {
                    break;
                }
            }
        }
    }

    private DrawableObject selectedProperty;

    private void initTileProperties() {

        DrawableGrid grid = panelProperties.getGrid();
        Database database = Database.getInstance();

        List<Property> properties = database.getProperties();

        Iterator<Property> iterator = properties.iterator();
        // reordering grid cell row to column
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < (grid.getWidth() - 1); x++) {
                if (iterator.hasNext()) {

                    final Property property = iterator.next();

                    grid.getCell(x, y).setUserObject(new DrawableObject(property.getPropertyId()) {

                        Image image = null;

                        @Override
                        public void draw(Graphics2D g, int x, int y, int width, int height) {
                            if (image == null) {
                                image = Assets.getInstance().get(property.getFile())[0];
                            }

                            g.drawImage(image, x, y, width, height, null);
                        }
                    });

                } else {
                    break;
                }
            }
        }

        // adding properties event
        panelProperties.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent event) {

                DrawableGrid grid = panelProperties.getGrid();

                DrawableCell selectedCell = grid.getCellScreen(event.getX(), event.getY());
                if (selectedCell.getUserObject() == null) {
                    return;
                }

                for (int x = 0; x < grid.getWidth(); x++) {
                    for (int y = 0; y < grid.getHeight(); y++) {
                        if (grid.getCell(x, y).equals(selectedCell)) {
                            grid.getCell(x, y).setSelected(true, Color.DARK_GRAY);
                        } else {
                            grid.getCell(x, y).setSelected(false);
                        }
                    }
                }

                selectedProperty = selectedCell.getUserObject();
            }
        });
    }
    private DrawableObject selectedFloor;

    private void initTileFloors() {

        final DrawableGrid grid = panelFloors.getGrid();

        Image[] images = Assets.getInstance().get("floors");

        int i = 0;
        // reordering grid cell row to column
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < (grid.getWidth() - 1); x++) {
                if (i < images.length) {
                    grid.getCell(x, y).setBackgroundObject(imageToObject(images[i]));
                } else {
                    break;
                }
                i++;
            }
        }

        // adding floors event
        panelFloors.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent event) {

                DrawableCell selectedCell = grid.getCellScreen(event.getX(), event.getY());
                if (selectedCell.getBackgroundObject() == null) {
                    return;
                }

                for (int x = 0; x < grid.getWidth(); x++) {
                    for (int y = 0; y < grid.getHeight(); y++) {
                        if (grid.getCell(x, y).equals(selectedCell)) {
                            grid.getCell(x, y).setSelected(true, Color.DARK_GRAY);
                        } else {
                            grid.getCell(x, y).setSelected(false);
                        }
                    }
                }

                selectedFloor = selectedCell.getBackgroundObject();
            }
        });
    }

    private void initDNDSupport() {
        initDragMonster();
        initDropMonster();
    }

    private void initDragMonster() {
        new DragSource().createDefaultDragGestureRecognizer(panelMonsters, DnDConstants.ACTION_COPY, new DragGestureListener() {

            @Override
            public void dragGestureRecognized(DragGestureEvent event) {

                DrawableGrid grid = panelMonsters.getGrid();

                Point point = event.getDragOrigin();

                DrawableCell cell = grid.getCellScreen(point.x, point.y);
                if (cell.getUserObject() == null) {
                    return;
                }

                Cursor cursor = null;
                if (event.getDragAction() == DnDConstants.ACTION_COPY) {
                    cursor = DragSource.DefaultCopyDrop;
                }

                DrawableObject object = cell.getUserObject();

                event.startDrag(cursor, new Transfer(object.getId()));
            }
        });
    }

    private void initDropMonster() {
        DropTarget drop = new DropTarget(panelGrid, DnDConstants.ACTION_COPY, new DropTargetAdapter() {

            @Override
            public void drop(DropTargetDropEvent event) {

                DrawableGrid grid = panelGrid.getGrid();
                if (grid == null) {
                    return;
                }

                Transferable transfer = event.getTransferable();
                try {
                    event.acceptDrop(DnDConstants.ACTION_COPY);

                    Object monsterId = transfer.getTransferData(Transfer.stringFlavor);
                    Point point = event.getLocation();

                    if (tabTileEditor.getSelectedIndex() == 0) {
                        Monster monster = new Monster();
                        monster.select(String.valueOf(monsterId));

                        DrawableArea area = grid.getSelectionScreen(point.x, point.y);
                        if (area == null) {
                            return;
                        }

                        Zone zone = area.getZone();

                        Database database = Database.getInstance();
                        database.addMonsterZone(zone, monster);
                        database.refreshMonsterZone();
                    }

                } catch (UnsupportedFlavorException | IOException e) {
                    System.err.println(e.getMessage());

                    event.rejectDrop();
                }

            }
        });
    }

    public DrawableObject[] imageToObject(Image... images) {
        DrawableObject[] objects = new DrawableObject[images.length];
        for (int i = 0; i < images.length; i++) {

            final Image image = images[i];

            objects[i] = new DrawableObject() {

                @Override
                public void draw(Graphics2D g, int x, int y, int width, int height) {
                    g.drawImage(image, x, y, width, height, null);
                }
            };

        }
        return objects;
    }

    public DrawableObject imageToObject(final Image image) {
        return new DrawableObject() {

            @Override
            public void draw(Graphics2D g, int x, int y, int width, int height) {
                g.drawImage(image, x, y, width, height, null);
            }
        };
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        groupSize = new javax.swing.ButtonGroup();
        panelGenerator = new javax.swing.JPanel();
        btnGenerate = new javax.swing.JButton();
        panelAppearance = new javax.swing.JPanel();
        btnLeft = new javax.swing.JButton();
        panelWalls = new graphics.panel.Grid32();
        btnRight = new javax.swing.JButton();
        panelControl = new javax.swing.JPanel();
        lbPartition = new javax.swing.JLabel();
        slidePartition = new javax.swing.JSlider();
        lbRandomness = new javax.swing.JLabel();
        slideRandomness = new javax.swing.JSlider();
        lbSize = new javax.swing.JLabel();
        radioSmall = new javax.swing.JRadioButton();
        radioLarge = new javax.swing.JRadioButton();
        panelTileEditor = new javax.swing.JPanel();
        tabTileEditor = new javax.swing.JTabbedPane();
        panelMonsters = new graphics.panel.Grid32();
        panelProperties = new graphics.panel.Grid32();
        panelFloors = new graphics.panel.Grid32();
        panelTileButton = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        panelInfo = new javax.swing.JPanel();
        lbStatFPS = new javax.swing.JLabel();
        lbInfo = new javax.swing.JLabel();
        panelGrid = new graphics.panel.Grid16();
        toolBar = new javax.swing.JToolBar();
        btnRun = new javax.swing.JButton();
        btnErase = new javax.swing.JButton();
        chkDebugMode = new javax.swing.JCheckBox();
        lbStat = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuItemNew = new javax.swing.JMenuItem();
        menuItemSave = new javax.swing.JMenuItem();
        menuItemLoad = new javax.swing.JMenuItem();
        menuItemExport = new javax.swing.JMenuItem();
        menuItemExit = new javax.swing.JMenuItem();
        menuEdit = new javax.swing.JMenu();
        menuItemClear = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBounds(new java.awt.Rectangle(0, 0, 0, 0));
        setName("frame"); // NOI18N
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelGenerator.setBorder(javax.swing.BorderFactory.createTitledBorder("GENERATOR"));

        btnGenerate.setText("GENERATE");
        btnGenerate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerateActionPerformed(evt);
            }
        });

        panelAppearance.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        btnLeft.setText("<");
        btnLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLeftActionPerformed(evt);
            }
        });
        panelAppearance.add(btnLeft);

        javax.swing.GroupLayout panelWallsLayout = new javax.swing.GroupLayout(panelWalls);
        panelWalls.setLayout(panelWallsLayout);
        panelWallsLayout.setHorizontalGroup(
            panelWallsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 65, Short.MAX_VALUE)
        );
        panelWallsLayout.setVerticalGroup(
            panelWallsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 33, Short.MAX_VALUE)
        );

        panelAppearance.add(panelWalls);

        btnRight.setText(">");
        btnRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRightActionPerformed(evt);
            }
        });
        panelAppearance.add(btnRight);

        panelControl.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbPartition.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        lbPartition.setText("PARTITION");

        slidePartition.setValue(0);

        lbRandomness.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        lbRandomness.setText("RANDOMNESS");

        slideRandomness.setValue(0);

        lbSize.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        lbSize.setText("SIZE");

        groupSize.add(radioSmall);
        radioSmall.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        radioSmall.setSelected(true);
        radioSmall.setText("SMALL");

        groupSize.add(radioLarge);
        radioLarge.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        radioLarge.setText("LARGE");

        javax.swing.GroupLayout panelControlLayout = new javax.swing.GroupLayout(panelControl);
        panelControl.setLayout(panelControlLayout);
        panelControlLayout.setHorizontalGroup(
            panelControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelControlLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbSize)
                    .addComponent(lbRandomness)
                    .addComponent(lbPartition))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(slidePartition, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(slideRandomness, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(panelControlLayout.createSequentialGroup()
                        .addComponent(radioSmall)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(radioLarge)))
                .addContainerGap())
        );
        panelControlLayout.setVerticalGroup(
            panelControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelControlLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(slidePartition, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbPartition))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(slideRandomness, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbRandomness))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radioSmall)
                    .addComponent(radioLarge)
                    .addComponent(lbSize))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelGeneratorLayout = new javax.swing.GroupLayout(panelGenerator);
        panelGenerator.setLayout(panelGeneratorLayout);
        panelGeneratorLayout.setHorizontalGroup(
            panelGeneratorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGeneratorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGeneratorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelAppearance, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelGeneratorLayout.createSequentialGroup()
                        .addGroup(panelGeneratorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(panelControl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 202, Short.MAX_VALUE)
                            .addComponent(btnGenerate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelGeneratorLayout.setVerticalGroup(
            panelGeneratorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelGeneratorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelAppearance, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelControl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnGenerate)
                .addContainerGap())
        );

        getContentPane().add(panelGenerator, new org.netbeans.lib.awtextra.AbsoluteConstraints(504, 13, 230, -1));

        panelTileEditor.setBorder(javax.swing.BorderFactory.createTitledBorder("TILE_EDITOR"));

        tabTileEditor.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabTileEditorStateChanged(evt);
            }
        });

        panelMonsters.setPreferredSize(new java.awt.Dimension(190, 160));

        javax.swing.GroupLayout panelMonstersLayout = new javax.swing.GroupLayout(panelMonsters);
        panelMonsters.setLayout(panelMonstersLayout);
        panelMonstersLayout.setHorizontalGroup(
            panelMonstersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 193, Short.MAX_VALUE)
        );
        panelMonstersLayout.setVerticalGroup(
            panelMonstersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 160, Short.MAX_VALUE)
        );

        tabTileEditor.addTab("MONSTERS", panelMonsters);

        javax.swing.GroupLayout panelPropertiesLayout = new javax.swing.GroupLayout(panelProperties);
        panelProperties.setLayout(panelPropertiesLayout);
        panelPropertiesLayout.setHorizontalGroup(
            panelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 193, Short.MAX_VALUE)
        );
        panelPropertiesLayout.setVerticalGroup(
            panelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 160, Short.MAX_VALUE)
        );

        tabTileEditor.addTab("PROPERTIES", panelProperties);

        javax.swing.GroupLayout panelFloorsLayout = new javax.swing.GroupLayout(panelFloors);
        panelFloors.setLayout(panelFloorsLayout);
        panelFloorsLayout.setHorizontalGroup(
            panelFloorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 193, Short.MAX_VALUE)
        );
        panelFloorsLayout.setVerticalGroup(
            panelFloorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 160, Short.MAX_VALUE)
        );

        tabTileEditor.addTab("FLOORS", panelFloors);

        btnAdd.setText("ADD_");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        panelTileButton.add(btnAdd);
        btnAdd.setText("ADD_MONSTER");

        javax.swing.GroupLayout panelTileEditorLayout = new javax.swing.GroupLayout(panelTileEditor);
        panelTileEditor.setLayout(panelTileEditorLayout);
        panelTileEditorLayout.setHorizontalGroup(
            panelTileEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTileEditorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabTileEditor)
                .addContainerGap())
            .addGroup(panelTileEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelTileEditorLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panelTileButton, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        panelTileEditorLayout.setVerticalGroup(
            panelTileEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTileEditorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabTileEditor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(58, Short.MAX_VALUE))
            .addGroup(panelTileEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTileEditorLayout.createSequentialGroup()
                    .addContainerGap(213, Short.MAX_VALUE)
                    .addComponent(panelTileButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );

        getContentPane().add(panelTileEditor, new org.netbeans.lib.awtextra.AbsoluteConstraints(504, 267, -1, -1));

        panelInfo.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panelInfo.setPreferredSize(new java.awt.Dimension(480, 32));

        lbStatFPS.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbStatFPS.setText("-");

        lbInfo.setText("-");

        javax.swing.GroupLayout panelInfoLayout = new javax.swing.GroupLayout(panelInfo);
        panelInfo.setLayout(panelInfoLayout);
        panelInfoLayout.setHorizontalGroup(
            panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbStatFPS)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 448, Short.MAX_VALUE)
                .addComponent(lbInfo)
                .addContainerGap())
        );
        panelInfoLayout.setVerticalGroup(
            panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(lbStatFPS, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                .addComponent(lbInfo))
        );

        getContentPane().add(panelInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 460, -1, -1));

        panelGrid.setPreferredSize(new java.awt.Dimension(480, 400));
        panelGrid.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panelGridMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panelGridMousePressed(evt);
            }
        });
        panelGrid.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                panelGridMouseMoved(evt);
            }
        });

        javax.swing.GroupLayout panelGridLayout = new javax.swing.GroupLayout(panelGrid);
        panelGrid.setLayout(panelGridLayout);
        panelGridLayout.setHorizontalGroup(
            panelGridLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 480, Short.MAX_VALUE)
        );
        panelGridLayout.setVerticalGroup(
            panelGridLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );

        getContentPane().add(panelGrid, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, -1));

        toolBar.setRollover(true);

        btnRun.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon/play.png"))); // NOI18N
        btnRun.setFocusable(false);
        btnRun.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRun.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRunActionPerformed(evt);
            }
        });
        toolBar.add(btnRun);

        btnErase.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon/eraser.png"))); // NOI18N
        btnErase.setFocusable(false);
        btnErase.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnErase.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnErase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEraseActionPerformed(evt);
            }
        });
        toolBar.add(btnErase);

        getContentPane().add(toolBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 480, -1));

        chkDebugMode.setText("DEBUG_MODE");
        chkDebugMode.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chkDebugModeStateChanged(evt);
            }
        });
        getContentPane().add(chkDebugMode, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 500, 100, -1));

        lbStat.setText("-");
        getContentPane().add(lbStat, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 504, -1, -1));

        menuFile.setText("File");

        menuItemNew.setText("New");
        menuItemNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemNewActionPerformed(evt);
            }
        });
        menuFile.add(menuItemNew);

        menuItemSave.setText("Save");
        menuItemSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemSaveActionPerformed(evt);
            }
        });
        menuFile.add(menuItemSave);

        menuItemLoad.setText("Load");
        menuItemLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemLoadActionPerformed(evt);
            }
        });
        menuFile.add(menuItemLoad);

        menuItemExport.setText("Export");
        menuItemExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemExportActionPerformed(evt);
            }
        });
        menuFile.add(menuItemExport);

        menuItemExit.setText("Exit");
        menuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemExitActionPerformed(evt);
            }
        });
        menuFile.add(menuItemExit);

        menuBar.add(menuFile);

        menuEdit.setText("Edit");

        menuItemClear.setText("Clear Map");
        menuItemClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemClearActionPerformed(evt);
            }
        });
        menuEdit.add(menuItemClear);

        menuBar.add(menuEdit);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLeftActionPerformed
        if (choice > 0) {
            choice--;
        } else {
            choice = 0;
        }

        initGenerator();
    }//GEN-LAST:event_btnLeftActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        if (tabTileEditor.getSelectedIndex() == 0) {
            formMonster = new FormMonster(this, true);
            formMonster.setLocationRelativeTo(null);
            formMonster.show();
            initTileMonsters();
        }
        if (tabTileEditor.getSelectedIndex() == 1) {
            formProperty = new FormProperty(this, true);
            formProperty.setLocationRelativeTo(null);
            formProperty.show();
            initTileProperties();
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRightActionPerformed
        if (choice < list.size() - 1) {
            choice++;
        } else {
            choice = list.size() - 1;
        }

        initGenerator();
    }//GEN-LAST:event_btnRightActionPerformed

    private void btnGenerateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerateActionPerformed

        final DrawableGrid grid = panelGrid.getGrid();
        grid.clear();

        final Database database = Database.getInstance();
        database.clear();

        // partition range between 0.1f - 0.25f
        final float maxPartition = 0.15f;
        final float defaultPartition = 0.25f;
        float partition = defaultPartition - maxPartition * (float) slidePartition.getValue() / 100.0f;

        // randomness range between 0.0f - 0.15f;
        final float maxRandomness = 0.15f;
        float randomness = maxRandomness * (float) slideRandomness.getValue() / 100.0f;

        // small wall, size will be 1, large wall size will be 2
        int size = 1;
        if (radioSmall.isSelected()) {
            size = 1;
        } else if (radioLarge.isSelected()) {
            size = 2;
        }

        // generate bsp dungeon
        BSPDungeon dungeon = new BSPDungeon(grid.getCells(), partition, randomness, size);
        dungeon.generate();

        BSPDungeon.RoomAccessor accessor = dungeon.new RoomAccessor();
        while (accessor.available()) {
            DrawableArea area = new DrawableArea(grid.getCellSize());
            accessor.access(area);
            grid.addSelection(area);
        }

        // set zone data to a temporary database
        for (DrawableArea selection : grid.getSelections()) {
            int x = selection.getX();
            int y = selection.getY();
            int width = selection.getWidth();
            int height = selection.getHeight();

            database.addZone(x, y, width, height);
            selection.setZone(database.getZone(x, y, width, height));
        }

        // set tile data to a temporary database
        final DrawableObject[] selectedWall = list.get(choice);

        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {

                final DrawableCell cell = grid.getCell(x, y);

                if (cell.isObstacle()) {
                    if (cell.getY() == (grid.getHeight() - 1)) {
                        cell.setUserObject(selectedWall[0]);

                        database.addTile(cell.getX(), cell.getY(), true, selectedWall[0].getId());
                    } else if (!grid.getCell(cell.getX(), cell.getY() + 1).isObstacle()) {
                        cell.setUserObject(selectedWall[0]);

                        database.addTile(cell.getX(), cell.getY(), true, selectedWall[0].getId());
                    } else {
                        cell.setUserObject(selectedWall[1]);

                        database.addTile(cell.getX(), cell.getY(), true, selectedWall[1].getId());
                    }
                }
            }
        }

        if (tabTileEditor.getSelectedIndex() == 0) {
            grid.showSelections(true);
            grid.showCells(false);
        }

        if (tabTileEditor.getSelectedIndex() == 1 || tabTileEditor.getSelectedIndex() == 2) {
            grid.showSelections(false);
            grid.showCells(true);
        }
    }//GEN-LAST:event_btnGenerateActionPerformed

    private void btnEraseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEraseActionPerformed
        if (btnErase.isSelected()) {
            btnErase.setSelected(false);
        } else {
            btnErase.setSelected(true);
        }
    }//GEN-LAST:event_btnEraseActionPerformed

    private void panelGridMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelGridMouseMoved

        DrawableGrid grid = panelGrid.getGrid();
        if (grid == null) {
            return;
        }

        if (tabTileEditor.getSelectedIndex() == 0) {

            DrawableArea selectedArea = grid.getSelectionScreen(evt.getX(), evt.getY());

            if (selectedArea != null) {
                for (DrawableArea area : grid.getSelections()) {
                    if (area.equals(selectedArea)) {
                        area.setSelected(true, Color.LIGHT_GRAY);
                    } else {
                        area.setSelected(false);
                    }
                }
            }
        }

        if (tabTileEditor.getSelectedIndex() == 1 || tabTileEditor.getSelectedIndex() == 2) {

            DrawableCell selectedCell = grid.getCellScreen(evt.getX(), evt.getY());

            if (selectedCell != null) {
                for (int x = 0; x < grid.getWidth(); x++) {
                    for (int y = 0; y < grid.getHeight(); y++) {
                        if (grid.getCell(x, y).equals(selectedCell)) {
                            grid.getCell(x, y).setSelected(true, Color.LIGHT_GRAY);
                        } else {
                            grid.getCell(x, y).setSelected(false);
                        }
                    }
                }
            }
        }
    }//GEN-LAST:event_panelGridMouseMoved

    private void panelGridMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelGridMouseExited

        DrawableGrid grid = panelGrid.getGrid();

        for (DrawableArea area : grid.getSelections()) {
            area.setSelected(false);
        }

        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                grid.getCell(x, y).setSelected(false);
            }
        }
    }//GEN-LAST:event_panelGridMouseExited

    private void tabTileEditorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabTileEditorStateChanged
        DrawableGrid grid = panelGrid.getGrid();

        if (grid == null) {
            return;
        }

        if (tabTileEditor.getSelectedIndex() == 0) {
            btnAdd.setVisible(true);
            btnAdd.setText("ADD_MONSTER");

            grid.showSelections(true);
            grid.showCells(false);
        }
        if (tabTileEditor.getSelectedIndex() == 1 || tabTileEditor.getSelectedIndex() == 2) {
            btnAdd.setVisible(true);
            btnAdd.setText("ADD_PROPERTY");

            if (tabTileEditor.getSelectedIndex() == 2) {
                btnAdd.setVisible(false);
            }

            grid.showSelections(false);
            grid.showCells(true);
        }

    }//GEN-LAST:event_tabTileEditorStateChanged

    private void chkDebugModeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chkDebugModeStateChanged
        if (chkDebugMode.isSelected()) {
            panelGrid.getGrid().showGrid(true);
            panelGrid.getGrid().showInfo(true);

            panelWalls.getGrid().showGrid(true);
            panelWalls.getGrid().showInfo(true);

            panelMonsters.getGrid().showGrid(true);
            panelMonsters.getGrid().showInfo(true);

            panelProperties.getGrid().showGrid(true);
            panelProperties.getGrid().showInfo(true);

            panelFloors.getGrid().showGrid(true);
            panelFloors.getGrid().showInfo(true);

        } else {
            panelGrid.getGrid().showGrid(false);
            panelGrid.getGrid().showInfo(false);

            panelWalls.getGrid().showGrid(false);
            panelWalls.getGrid().showInfo(false);

            panelMonsters.getGrid().showGrid(false);
            panelMonsters.getGrid().showInfo(false);

            panelProperties.getGrid().showGrid(false);
            panelProperties.getGrid().showInfo(false);

            panelFloors.getGrid().showGrid(false);
            panelFloors.getGrid().showInfo(false);
        }
    }//GEN-LAST:event_chkDebugModeStateChanged

    private void panelGridMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelGridMousePressed

        DrawableGrid grid = panelGrid.getGrid();
        Database database = Database.getInstance();

        DrawableCell selectedCell = grid.getCellScreen(evt.getX(), evt.getY());

        if (selectedCell.isObstacle()) {
            return;
        }

        // event when tab tile editor is monster
        if (tabTileEditor.getSelectedIndex() == 0) {
            DrawableArea selectedArea = grid.getSelectionScreen(evt.getX(), evt.getY());

            if (selectedArea == null) {
                return;
            }

            if (btnErase.isSelected()) {
                database.removeMonsterZone(selectedArea.getZone());
                database.refreshMonsterZone();
            }
        }
        // event when tab tile editor is property
        if (tabTileEditor.getSelectedIndex() == 1) {
            if (evt.getButton() == MouseEvent.BUTTON1) {
                if (btnErase.isSelected()) {
                    selectedCell.setUserObject(null);

                    database.removeTileProperty(selectedCell.getX(), selectedCell.getY());
                    database.refreshTileProperty();
                } else {
                    if (selectedProperty != null) {
                        if (selectedCell.getBackgroundObject() != null) {
                            selectedCell.setUserObject(selectedProperty);

                            database.addTileProperty(selectedCell.getX(), selectedCell.getY(), selectedProperty.getId());
                            database.refreshTileProperty();
                        }
                    }
                }
            }
            if (evt.getButton() == MouseEvent.BUTTON3) {
                DrawableCell[][] selectedCells = grid.getCellSelectionScreen(evt.getX(), evt.getY());

                if (selectedCells == null) {
                    return;
                }

                if (btnErase.isSelected()) {
                    for (DrawableCell[] cells : selectedCells) {
                        for (DrawableCell cell : cells) {
                            cell.setUserObject(null);

                            database.removeTileProperty(cell.getX(), cell.getY());
                            database.refreshTileProperty();
                        }
                    }
                }
            }
        }
        // event when tab tile editor is floor
        if (tabTileEditor.getSelectedIndex() == 2) {
            if (evt.getButton() == MouseEvent.BUTTON1) {
                if (btnErase.isSelected()) {
                    selectedCell.setBackgroundObject(null);

                    database.removeTile(selectedCell.getX(), selectedCell.getY());
                } else {

                    if (selectedFloor != null) {
                        selectedCell.setBackgroundObject(selectedFloor);

                        database.addTile(selectedCell.getX(), selectedCell.getY(), false, selectedFloor.getId());
                    }
                }
            }
            if (evt.getButton() == MouseEvent.BUTTON3) {
                DrawableCell[][] selectedCells = grid.getCellSelectionScreen(evt.getX(), evt.getY());

                if (selectedCells == null) {
                    return;
                }

                if (btnErase.isSelected()) {
                    for (DrawableCell[] cells : selectedCells) {
                        for (DrawableCell cell : cells) {
                            cell.setBackgroundObject(null);

                            database.removeTile(cell.getX(), cell.getY());
                        }
                    }
                } else {
                    for (DrawableCell[] cells : selectedCells) {
                        for (DrawableCell cell : cells) {
                            cell.setBackgroundObject(selectedFloor);

                            database.addTile(cell.getX(), cell.getY(), false, selectedFloor.getId());
                        }
                    }
                }
            }
        }
    }//GEN-LAST:event_panelGridMousePressed

    private void menuItemClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemClearActionPerformed
        Database.getInstance().clear();
        panelGrid.getGrid().clear();
    }//GEN-LAST:event_menuItemClearActionPerformed

    private void menuItemSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemSaveActionPerformed
        formSave = new FormSave(this, true);
        formSave.setLocationRelativeTo(null);
        formSave.show();

        if (formSave.isSave()) {
            Database database = Database.getInstance();
            database.save(formSave.getName(), formSave.getDescription(), formSave.getDifficulty(), lbStat);
        }

    }//GEN-LAST:event_menuItemSaveActionPerformed

    private void menuItemLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemLoadActionPerformed

        formLoad = new FormLoad(this, true);
        formLoad.setLocationRelativeTo(null);
        formLoad.show();

        if (formLoad.isLoad()) {
            Database database = Database.getInstance();
            database.load(formLoad.getMapId());
            DrawableGrid grid = panelGrid.getGrid();
            grid.clear();

            // set data tiles base of temporary database
            for (int x = 0; x < grid.getWidth(); x++) {
                for (int y = 0; y < grid.getHeight(); y++) {

                    Tile tile = database.getTile(x, y);

                    grid.getCell(x, y).setBackgroundObject(DrawableObject.get(tile.getTileId()));

                    if (!tile.getPropertyId().equals("")) {
                        grid.getCell(x, y).setUserObject(DrawableObject.get(tile.getPropertyId()));
                    }
                }
            }

            // set data zones base of temporary database
            for (Zone zone : database.getZones()) {
                DrawableArea area = new DrawableArea(grid.getCellSize());
                area.setBound(zone.getBoundX(), zone.getBoundY(), zone.getBoundWidth(), zone.getBoundHeight());
                area.setZone(zone);

                grid.addSelection(area);
            }

            if (tabTileEditor.getSelectedIndex() == 0) {
                grid.showSelections(true);
                grid.showCells(false);
            }
            if (tabTileEditor.getSelectedIndex() == 1 || tabTileEditor.getSelectedIndex() == 2) {
                grid.showSelections(false);
                grid.showCells(true);
            }
        }


    }//GEN-LAST:event_menuItemLoadActionPerformed

    private void menuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemExitActionPerformed
        Database.getInstance().close();
        System.exit(0);
    }//GEN-LAST:event_menuItemExitActionPerformed

    private void menuItemExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemExportActionPerformed

        formExport = new FormExport(this, true);
        formExport.setLocationRelativeTo(null);
        formExport.setPrint(panelGrid.getGrid());
        formExport.show();


    }//GEN-LAST:event_menuItemExportActionPerformed

    private void menuItemNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemNewActionPerformed
        Database.getInstance().load(new entities.Map().getMapId());
        panelGrid.getGrid().clear();
    }//GEN-LAST:event_menuItemNewActionPerformed

    private void btnRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRunActionPerformed

        Game game = new Game();
        game.setVisible(true);
        game.setResizable(false);
        game.setLocationRelativeTo(null);

    }//GEN-LAST:event_btnRunActionPerformed
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Application.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Application.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Application.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Application.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                Application application = new Application();
                application.setTitle("Level Editor");
                application.setVisible(true);
                application.setResizable(false);

                // set to full screen mode
//                Toolkit toolkit = Toolkit.getDefaultToolkit();
//                application.setSize(toolkit.getScreenSize());
                application.setLocationRelativeTo(null);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnErase;
    private javax.swing.JButton btnGenerate;
    private javax.swing.JButton btnLeft;
    private javax.swing.JButton btnRight;
    private javax.swing.JButton btnRun;
    private javax.swing.JCheckBox chkDebugMode;
    private javax.swing.ButtonGroup groupSize;
    private javax.swing.JLabel lbInfo;
    private javax.swing.JLabel lbPartition;
    private javax.swing.JLabel lbRandomness;
    private javax.swing.JLabel lbSize;
    private javax.swing.JLabel lbStat;
    private javax.swing.JLabel lbStatFPS;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menuEdit;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenuItem menuItemClear;
    private javax.swing.JMenuItem menuItemExit;
    private javax.swing.JMenuItem menuItemExport;
    private javax.swing.JMenuItem menuItemLoad;
    private javax.swing.JMenuItem menuItemNew;
    private javax.swing.JMenuItem menuItemSave;
    private javax.swing.JPanel panelAppearance;
    private javax.swing.JPanel panelControl;
    private graphics.panel.Grid32 panelFloors;
    private javax.swing.JPanel panelGenerator;
    private graphics.panel.Grid16 panelGrid;
    private javax.swing.JPanel panelInfo;
    private graphics.panel.Grid32 panelMonsters;
    private graphics.panel.Grid32 panelProperties;
    private javax.swing.JPanel panelTileButton;
    private javax.swing.JPanel panelTileEditor;
    private graphics.panel.Grid32 panelWalls;
    private javax.swing.JRadioButton radioLarge;
    private javax.swing.JRadioButton radioSmall;
    private javax.swing.JSlider slidePartition;
    private javax.swing.JSlider slideRandomness;
    private javax.swing.JTabbedPane tabTileEditor;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables
}
