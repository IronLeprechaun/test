package com.dvreiter.starassault.Menu.LE;	

import com.dvreiter.starassault.*;
import android.os.*;
import android.app.*;
import com.badlogic.gdx.utils.*;
import com.dvreiter.starassault.Player.*;
import java.io.*;
import org.flixel.*;
import org.flixel.event.*;
import org.flixel.ui.*;
import com.dvreiter.starassault.Menu.LE.*;
import com.dvreiter.starassault.Menu.*;
import android.text.method.*;
import com.badlogic.gdx.maps.tiled.*;
import org.flixel.system.*;
import android.graphics.*;
import android.content.*;
import android.widget.*;
import android.accounts.*;


public class PlayStateLe2 extends FlxState 
{	

		private static final int TILE_WIDTH = 16;
		private static final int TILE_HEIGHT = 16;

		private FlxText coords;

		private int[] ScreenData;

		//private FlxObject[] highlightBox;
		private FlxSprite[] highlightBox;
//	private MainActivity intent;

		public static PrintWriter pr;

		//new Level info
		public static boolean loadLevel;
		public static int levelWidth,levelHeight;
		public static String levelName;
		public static Boolean playerMode;
		private String color = "0xFF00CCF";
		private Boolean isPlayer = false;
		private Boolean isPortal = false;
		private boolean isPortalUnlock = false;
		private boolean isLockDoor = false;
		private boolean isLockDKey = false;
		private boolean ifGravToggle = false;
		private boolean allCode = true;
		private FlxTimer timer;
		private boolean whichPlayer;

		private String playerInfo,coinInfo,lockDoorInfo,lockDKeyInfo;
		private String mintCandyPUInfo,endPortalInfo,endPortalCoinInfo,skeltonInfo;
		private String spikesInfo,leverInfo,temInfo,termimatorInfo;

		public FlxButton pause;
		public FlxTilemap level;
		public FlxTileblock block;
		private FlxButton closeMenu;
		private FlxButton Menu;
		private FlxButton typeBtn; 
		private FlxButton itemBtn;
		private FlxButton itemBackBtn;
		private FlxButton ItemDisplay;
		private FlxButton savebtn;
		private FlxButton BlockOptions;
		private FlxButton backbtn;
		private FlxButton BackCheckBtn,BackCheckBtnNo;
		private FlxButton switchPlayer;
		private FlxButton resetbtn;
		private FlxButton sharebtn;
		private int BlockOMode;
		private int blockX,blockY;

		private int lastPBlockX,lastPBlockY;

		private int i,x,y;
		private int Item, Type;
		private String []blockNames;
		private String ifSaved = " ";
		public static final String FntRobotoRegular = "Roboto-Regular.ttf";

		protected FlxEmitter _littleGibs;

		FlxVirtualPad pad;
		PlayerLE playerLE;
		Player player;
		FlxGroup _bullets;
		LELevelLoader LoadLevel;

		ErrorReporter error;

		//Advance stuff
		private int [] [] [] levelData;

		private int blockMax = 21;

		private FlxText ifSavedTxt;

		private String[] entityNames;

		private String[] entityFileNames;

		private String mageInfo;

		private String turretInfo;

		@Override
		public void create()
		{					
				error = new	ErrorReporter();

				if (loadLevel == true)
				{
						LoadLevel = new LELevelLoader();
						try
						{
								LoadLevel.loadLevel(levelName);

								int data[] = new int[LoadLevel.getLevelWidth() * LoadLevel.getLevelHeight()];
								data = LoadLevel.getLevel();
								levelWidth = LoadLevel.getLevelWidth();
								levelHeight = LoadLevel.getLevelHeight();
								level = new FlxTilemap();
								level.loadMap(FlxTilemap.arrayToCSV(new IntArray(data), levelWidth), "tilemap.png", 16, 16);// 50x30
								add(level);
						}
						catch (Exception e)
						{
								error.reportError(e.toString());
								FlxG.switchState(new PlayStateLESettings());	
						}
				}
				else
				{
						int data[] = new int[levelHeight * levelWidth];
						level = new FlxTilemap();
						level.loadMap(FlxTilemap.arrayToCSV(new IntArray(data), levelWidth), "tilemap.png", 16, 16);// 50x30
						add(level);
				}

				FlxG.setBgColor(0xFFE0CCFF);
				FlxG.mouse.show();

				// One Block Debugged
				//highlightBox = new FlxObject(0,0,TILE_WIDTH, TILE_HEIGHT);
				//highlightBox = new FlxObject[2];
				highlightBox = new FlxSprite[levelWidth];
				drawBox();
				//level end

				String[] tempBlockNames = {"Eraser","Grass1","Bricks1","Bricks2","GrassLeft","GrassRight","GrassAlone","DirtStone1","GrassSolid","FullDirt","Stone1","NetherBlock", "Grass2","Invalid Block","Invalid Block","Invalid Block","Invalid Block","Invalid Block","DirtStone2","Invalid Block","Invalid Block","Stone2"};	
				blockNames = tempBlockNames;
				String[] tempEntityNames = {"Player","Coin","Locked Door", "Key", "PepperMint", "EndPortal","EndPortalUnlock","Skeleton","Spikes","Switch", "Mage" , "Terminater" ,"Turret" };
				entityNames = tempEntityNames;
				String[] tempEntityFileNames = {"budderking","coin","keylock", "lockkey" , "peppermintpowerup" , "portal" , "Portalcoin" , "skeletonminion", "spikes" , "switch" , "mage" , "terminator", "switch"};
				entityFileNames = tempEntityFileNames;
				levelData = new int[levelWidth] [levelHeight] [3];

				coords = new FlxText(10, 10, 90, "Coords:");	
				coords.setSize(10);
				coords.setColor(0x3a5c39);
				coords.scrollFactor.x = coords.scrollFactor.y = 0;
				add(coords);

				ifSavedTxt = new FlxText(40, 60, 100, "");
				ifSavedTxt.setSize(16);
				add(ifSavedTxt);
				timer = new FlxTimer();

				_littleGibs = new FlxEmitter();
				_littleGibs.setXSpeed(-150, 150);
				_littleGibs.setYSpeed(-200, 0);
				_littleGibs.setRotation(-720, -720);
				_littleGibs.gravity = 350;
				_littleGibs.bounce = 0.5f;
				_littleGibs.makeParticles("gibs.png", 100, 10, true, 0.5f);

				sharebtn = new FlxButton(230, 5, "Share", new IFlxButton(){@Override public void callback(){onShare();}});
				sharebtn.scrollFactor.x = sharebtn.scrollFactor.y = 0;
				add(sharebtn);		

				resetbtn = new FlxButton(310,45,"Clear", new IFlxButton(){@Override public void callback(){onClear();}});
				resetbtn.scrollFactor.x = resetbtn.scrollFactor.y = 0;
				add(resetbtn);

				savebtn = new FlxButton(310, 25, "Save", new IFlxButton(){@Override public void callback(){saveGame(levelName);}});
				savebtn.scrollFactor.x = savebtn.scrollFactor.y = 0;
				add(savebtn);	

				backbtn = new FlxButton(310, 5, "Back", new IFlxButton(){@Override public void callback(){onBack();}});
				backbtn.scrollFactor.x = backbtn.scrollFactor.y = 0;
				add(backbtn);	

				Menu = new FlxButton(310, 30, " ", new IFlxButton(){@Override public void callback(){onMenu();}});
				Menu.loadGraphic("LEShip.png");
				add(Menu);	
				Menu.visible = false;

				itemBackBtn = new FlxButton(310, 65, "Back A Item", new IFlxButton(){@Override public void callback(){onItemBack();}});
				itemBackBtn.scrollFactor.x = itemBackBtn.scrollFactor.y = 0;
				add(itemBackBtn);

				itemBtn = new FlxButton(310, 85, "Grass", new IFlxButton(){@Override public void callback(){onItem();}});
				itemBtn.scrollFactor.x = itemBtn.scrollFactor.y = 0;
				add(itemBtn);

				ItemDisplay = new FlxButton(310, 105, " ");
				ItemDisplay.scrollFactor.x = ItemDisplay.scrollFactor.y = 0;
				add(ItemDisplay);
				ItemDisplay.width = 100;
				ItemDisplay.height = 100;

				typeBtn = new FlxButton(310, 121, "Blocks", new IFlxButton(){@Override public void callback()
								{onType();}});
				typeBtn.scrollFactor.x = typeBtn.scrollFactor.y = 0;
				add(typeBtn);

				BackCheckBtn = new FlxButton(160, 90, "Are You sure\n Â  Â  Yes", new IFlxButton(){@Override public void callback()
								{onBackCheckBtn();}});
				BackCheckBtn.scrollFactor.x = BackCheckBtn.scrollFactor.y = 0;
				add(BackCheckBtn);

				BackCheckBtnNo = new FlxButton(160, 110, "No", new IFlxButton(){@Override public void callback()
								{onBackCheckBtnNo();}});
				BackCheckBtnNo.scrollFactor.x = BackCheckBtnNo.scrollFactor.y = 0;
				add(BackCheckBtnNo);

				switchPlayer = new FlxButton(310, 161, "ChangePlayer", new IFlxButton(){@Override public void callback()
								{onChangePlayer();}});
				switchPlayer.scrollFactor.x = switchPlayer.scrollFactor.y = 0;
				add(switchPlayer);

				BackCheckBtnNo.visible = false;
				BackCheckBtn.visible = false;

				closeMenu = new FlxButton(310, 141, "Close Menu", new IFlxButton(){@Override public void callback()
								{onCloseMenu();}});
				closeMenu.scrollFactor.x = closeMenu.scrollFactor.y = 0;
				add(closeMenu);

				pad = new FlxVirtualPad(FlxVirtualPad.DPAD_FULL, FlxVirtualPad.A_B_X_Y);
				pad.setAlpha(0.5f);

				_bullets = new FlxGroup();
				playerLE = new PlayerLE(2 * 16, 0, 16, 16, _bullets, _littleGibs, pad);

				player = new Player(0, 0, 16, 16, _bullets, _littleGibs, pad);
				player.setHasGravityToggle(true);
				player.setHasFlyingToggle(false);
				player.active = false;
				player.visible = false;	

				FlxG.camera.follow(playerLE, FlxCamera.STYLE_PLATFORMER);
				FlxG.camera.setBounds(0, 0, levelWidth * 16, levelHeight * 16, true);// 1st 400,240 Â 2nd 800,240, 3rd 1200,48

				add(player);
				add(playerLE);					
				add(_littleGibs);
				add(pad);
				add(_bullets);

				onItem();
		}

		public void drawBox(){
				// World Grid Debug Size
				for(i = 0; i < highlightBox.length; i++){
						for(x = 0; x <= (levelWidth*16); x += 16){ //x=400 but changed for custom resizement

								if(x == (levelWidth*16) && y <= (levelHeight*16)){//x=400,y=240 default size but now changed for custom resizement
										x=0;
										y+=16;
								}
								highlightBox[i] = new FlxSprite(x,y);
								highlightBox[i].loadGraphic("OutlineHitbox.png", true, true, 16, 16);
								add(highlightBox[i]);
								//for(int y = 0; y < highlightBox.length; y += 16){
								//	highlightBox[i] = new FlxObject(x,0,TILE_WIDTH,TILE_HEIGHT);
								// Â highlightBox[i].drawDebug();
								//		}
						}
				}
		}

		@Override
		public void update()
		{	
				//highlightBox.x = (float) (Math.floor(FlxG.mouse.x / TILE_WIDTH) * TILE_WIDTH);
				//highlightBox.y = (float) (Math.floor(FlxG.mouse.y / TILE_HEIGHT) * TILE_HEIGHT);

				if (FlxG.keys.pressed("BACK"))
				{onMenu();}//If back button is pressed

				if (playerLE.visible = true)
				{
						blockX = (int)FlxG.mouse.x / TILE_WIDTH;
						blockY = (int)FlxG.mouse.y / TILE_HEIGHT;
						//Get data map coordinate

						coords.setText("Coords:" + "\n X:" + BackCheckBtn.x + "\n Y:" + BackCheckBtn.y + "\n" + Item + "\n" + isPlayer + "\nTile\n" + level.getTile(blockX, blockY) + "\nmouseX/Y" + FlxG.mouse.screenX / 16 + "/" + FlxG.mouse.screenY / 16);
						ifSavedTxt.setText(ifSaved);


						if (FlxG.mouse.pressed() && FlxG.mouse.screenX < itemBackBtn.x || FlxG.mouse.screenX > itemBackBtn.x + itemBackBtn .width)
						{	
								if (FlxG.mouse.screenX > 85 || FlxG.mouse.screenY < 125)
								{
										if (FlxG.mouse.screenX < 100 || FlxG.mouse.screenY > 15)
												onCloseMenu();
								}
						}
						if (FlxG.mouse.pressed() && typeBtn.visible == false)
						{
								placeItem(blockX, blockY);
								lastPBlockX = blockX;
								lastPBlockY = blockY;
						}
						Menu.x = playerLE.x;
						Menu.y = playerLE.y;
				}
				super.update();	
				FlxG.collide(level, player);
		}

		/*	@Override
		 public void draw()
		 {
		 super.draw();
		 //To Show Grid and Debug All blocks
		 for(int i = 0; i < highlightBox.length; i++){
		 highlightBox[i].drawDebug();
		 // Â highlightBox[0].drawDebug();
		 //	highlightBox[1].drawDebug();
		 }
		 }

		 */
		private void placeItem(int mx, int my)
		{
				if (level.getTile(mx, my) == 22)
				{isPlayer = false;}
				if (level.getTile(mx, my) == 24)
				{isLockDoor = false;}
				if (level.getTile(mx, my) == 25)
				{isLockDKey = false;}
				if (level.getTile(mx, my) == 27)
				{isPortal = false;}
				if (level.getTile(mx, my) == 28)
				{isPortalUnlock = false;}

				if (Type == 0)
				{level.setTile(mx, my, Item, true);}

				if (Type == 1)
				{
						switch (Item + 22)
						{
								case 22:if(isPlayer == false)
										{
												level.setTile(mx, my, Item + 22, true);
												isPlayer = true;
										}break;
								case 23:level.setTile(mx, my, Item + 22, true);
										break;
								case 24:if (isLockDoor == false)
										{
												level.setTile(mx, my, Item + 22, true);
												isLockDoor = true;
										}
										break;
								case 25:if (isLockDKey == false)
										{
												level.setTile(mx, my, Item + 22, true);
												isLockDKey = true;
										}
										break;
								case 26:level.setTile(mx, my, Item + 22, true);
										break;
								case 27:if (isPortal == false)
										{
												level.setTile(mx, my, Item + 22, true);
												isPortal = true;
										}
										break;
								case 28:if (isPortalUnlock == false)
										{
												level.setTile(mx, my, Item + 22, true);
												isPortalUnlock = true;
										}
										break;
								default:level.setTile(mx, my, Item + 22, true);
										break;
						}
				}


		}

		public void updateItemDisplay()
		{
				try
				{
						if (Type == 0)
						{
								typeBtn.label.setText("Blocks");		
								if (Item > blockMax)
								{Item = 0;}
								if (Item < 0)
								{Item = blockMax;}		

								itemBtn.label.setText(blockNames[Item]);
								ItemDisplay.loadGraphic("BlockTextures/" + blockNames[Item] + ".png");				
						}
						if (Type == 1)
						{
								itemBtn.label.setText("Entities");
								if (Item > entityNames.length)
								{Item = 0;}
								if (Item < 0)
								{Item = entityNames.length;}

								itemBtn.label.setText(entityNames[Item]);
								ItemDisplay.loadGraphic(entityFileNames[Item] + ".png");			
						}
				}
				catch (Exception e)
				{
						error.reportError(e.toString());
				}
		}



		public int[] CSVtoArray(int width, int height, boolean allBlocks)
		{
				playerInfo = null;
				coinInfo = null;
				lockDoorInfo = null;
				lockDKeyInfo = null;
				mintCandyPUInfo = null;
				endPortalInfo = null;
				endPortalCoinInfo = null;
				skeltonInfo = null;
				spikesInfo = null;
				leverInfo = null;
				temInfo = null;
				termimatorInfo = null;
				mageInfo = null;
				turretInfo = null;

				ScreenData = new int[width * height];
				int round = 0;

				for (int y = 0; y <= height - 1; y = y + 1)
				{
						for (int x = 0; x <= width - 1; x = x + 1)
						{
								ScreenData[round] = level.getTile(x , y);	
								if (ScreenData[round] == 22)
								{
										if (allBlocks)
										{
												ScreenData[round] = 0;
												playerInfo = "_player = new Player(" + x * 16 + "," + y * 16 + ",16,16,_bullets,_littleGibs, pad);" + System.lineSeparator() + "_player.setHasGravityToggle(" + ifGravToggle + ");" + System.lineSeparator() + "_player.setHasFlyingToggle(true);";
										}
								}
								else if (ScreenData[round] == 23)
								{
										if (coinInfo == null)
										{
												coinInfo = "coins = new FlxGroup();";
										}
										coinInfo = coinInfo + " \ncreateCoin(" + x * 16 + "," + y * 16 + ");";
										if (allBlocks)
										{
												ScreenData[round] = 0;	
										}
								}
								else if (ScreenData[round] == 24)
								{	if (allBlocks)
										{
												ScreenData[round] = 0;
										}
										lockDoorInfo = "lock = new FlxSprite(" + x * 16 + "," + y * 16 + ");\nlock.loadGraphic(" + '"' + "keylock.png" + '"' + ", true, true, 16, 16);\n Â lock.immovable = true;\nadd(lock);";	
								}
								else if (ScreenData[round] == 25)
								{
										if (allBlocks)
										{
												ScreenData[round] = 0;
										}
										lockDKeyInfo = "key = new FlxSprite(" + x * 16 + "," + y * 16 + ");\nkey.loadGraphic(" + '"' + "lockkey.png" + '"' + ", true, true, 16, 16); Â \nkey.immovable = true;\nadd(key);";	
								}
								else if (ScreenData[round] == 26)
								{
										if (allBlocks)
										{
												ScreenData[round] = 0;	
										}
										if (mintCandyPUInfo == null)
										{
												mintCandyPUInfo = "ppowerupp = new FlxGroup();\n";	
										}
										mintCandyPUInfo = mintCandyPUInfo + "createPpowerup(" + x * 16 + "," + y * 16 + ");\n";			
								}
								else if (ScreenData[round] == 27)//here
								{
										endPortalInfo = "portal = new FlxSprite(50, 50);//50\nportal.loadGraphic(" + '"' + "portal.png" + '"' + ", true, true, 16, 16);\nportal.addAnimation(" + '"' + "spinning" + '"' + ", new int[]{0, 1, 2}, 6, true);\nportal.play(" + '"' + "spinning" + '"' + ");\nportal.immovable = true;\n" + "portal.exists = " + !isPortalUnlock + ";\n" + "add(portal);";
										if (allBlocks)
										{
												ScreenData[round] = 0;
										}
								}
								else if (ScreenData[round] == 28)
								{
										endPortalCoinInfo = "portalcoin = new FlxSprite(170, 80);//80\nportalcoin.loadGraphic(" + '"' + "Portalcoin.png" + '"' + ", true, true, 16, 16);\nportalcoin.addAnimation(" + '"' + "rotate" + '"' + ", new int[]{0, 1, 2}, 4, true);\nportalcoin.play(" + '"' + "rotate" + '"' + ");\nadd(portalcoin);\n";
										if (allBlocks)
										{
												ScreenData[round] = 0;
										}
								}
								else if (ScreenData[round] == 29)
								{
										if (skeltonInfo == null)
										{skeltonInfo = "skeletons = new FlxGroup();\n";}
										skeltonInfo = skeltonInfo + "createSkelton(" + x * 16 + "," + y * 16 + ");";
										if (allBlocks)
										{ScreenData[round] = 0;}
								}
								else if (ScreenData[round] == 30)//spikes
								{
										if (spikesInfo == null)
										{
												spikesInfo = "spikes = new FlxGroup();\n";
										}
										spikesInfo = spikesInfo + " createSpike(" + x * 16 + "," + y * 16 + ",0);\n";
										if (allBlocks)
										{
												ScreenData[round] = 0;
										}
								}
								else if (ScreenData[round] == 31)
								{
										if (allBlocks)
										{
												ScreenData[round] = 0;
										}
										leverInfo = "wallSwitch = new FlxSprite(" + x * 16 + "," + y * 16 + ");\nwallSwitch.loadGraphic(" + '"' + "switch.png" + '"' + ", true, true, 16, 16);\nwallSwitch.immovable = true;\nadd(wallSwitch);";
								}
								else if (ScreenData[round] == 32)
								{
										if(mageInfo == null)
										{
												mageInfo += "mages = new FlxGroup();\n";
										}
										mageInfo += "createMage("+ x +","+ y+ ",_bullets,1);\n";

										if (allBlocks)
										{
												ScreenData[round] = 0;
										}
								}
								else if (ScreenData[round] == 33)
								{
										if (allBlocks)
										{
												ScreenData[round] = 0;
										}
										if (termimatorInfo == null)
										{
												termimatorInfo = "enemies = new FlxGroup();\n";
										}
										termimatorInfo = termimatorInfo + "createEnemy(" + x * 16 + "," + y * 16 + ",500);\n";
								}
								round++;
						}
				}
				return ScreenData;	
		}

		private void saveGame( String fileName)
		{
				try
				{		
						int [] views;
						views = new int [ levelWidth * levelHeight];
						views = CSVtoArray(levelWidth, levelHeight, false);

						if (fileName == null)
						{fileName = "test.txt";}

						final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Bobrun/");

						if (!dir.exists())
								dir.mkdirs(); 


						final File myFile = new File(dir, fileName);


						if (!myFile.exists()) 
								myFile.createNewFile();

						PrintWriter pr = new PrintWriter(myFile);  

						for (int i=0; i < views.length ; i++)
						{			
								if (i == 0)
								{

										pr.print("%" + playerMode);
										pr.print("|" + levelWidth + "," + levelHeight + "}");
										pr.print("~" + System.currentTimeMillis() + "}");
										pr.print("{");
								}
								pr.print(views[i]);
								if (i != views.length - 1)
								{
										pr.print(",");
								}
								else
								{
										pr.print("}");
								}
						}
						pr.close();
				}catch (Exception e){error.reportError(e.toString());}		

				if(allCode)		
				{
						try
						{		
								int [] views;
								views = new int [ levelWidth * levelHeight];
								views = CSVtoArray(levelWidth, levelHeight, false);

								if (fileName == null)
								{fileName = "test.txt";}

								final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Bobrun/");
								if (!dir.exists())
										dir.mkdirs(); 

								final File myFile = new File(dir, "CodeFor" + fileName);
								if (!myFile.exists()) 
										myFile.createNewFile();

								PrintWriter pr = new PrintWriter(myFile);  

								for (int i=0; i < views.length ; i++)
								{			
										if (i == 0 && allCode == true)
										{	
												pr.print(info.prImportVars(endPortalCoinInfo, spikesInfo, coinInfo, leverInfo, lockDKeyInfo, termimatorInfo, skeltonInfo, mintCandyPUInfo, color,mageInfo, fileName));
												pr.println("int data [] = {");	
										}
										pr.print(views[i]);
										if (i != views.length - 1)
										{
												pr.print(",");
										}
										else
										{
												pr.print("}");
												pr.print(info.tileMaptxt(levelWidth));
												pr.print(info.afterArrayTxt(endPortalCoinInfo, spikesInfo, coinInfo, leverInfo, lockDKeyInfo, termimatorInfo, skeltonInfo, mintCandyPUInfo, color, endPortalInfo, lockDoorInfo, playerInfo, levelWidth, levelHeight, mageInfo));
										}
								}
								pr.close();
						}catch (Exception e){error.reportError(e.toString());}	
				}
				if (ifSaved == " ")
				{ifSaved = "Saved";}
				timer.start(3, 1, Tstop);
		}

		//Timer fix
		IFlxTimer Tstop = new IFlxTimer(){@Override
				public void callback(FlxTimer flxTimer)
				{ifSaved = " ";
				}
		};

		//Buttons
		private void onClear(){
				int[] data = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
						0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
				level.loadMap(FlxTilemap.arrayToCSV(new IntArray(data), levelWidth), "tilemap.png", 16, 16);
		}

		private void onShare(){
				/*	Intent i = new Intent(Intent.ACTION_SEND);
				 i.setType("message/rfc822");
				 i.putExtra(Intent.EXTRA_EMAIL Â , new String[]{"recipient@example.com"});
				 i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
				 i.putExtra(Intent.EXTRA_TEXT Â  , "body of email");*/
				/*	try {
				 startActivity(Intent.createChooser(i, "Send mail..."));
				 } catch (android.content.ActivityNotFoundException ex) {
				 Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
				 }*/
				FlxG.switchState(new ShareLevels());

		}

		private void onItem()
		{
				Item++;
				updateItemDisplay();
		}

		private void onBack()
		{
				BackCheckBtnNo.visible = true;
				BackCheckBtn.visible = true;
		}

		private void onBackCheckBtn()
		{
				FlxG.switchState(new MenuState());
		}

		private void onBackCheckBtnNo()
		{
				BackCheckBtnNo.visible = false;
				BackCheckBtn.visible = false;
		}
		private void onItemBack()
		{
				Item = Item - 1;
				updateItemDisplay();	
		}

		private void onType()
		{
				Type++;
				if (Type > 1)
				{Type = 0;}
				updateItemDisplay();
		}


		private void onMenu()
		{
				if (!FlxG.keys.pressed("BACK")){level.setTile(lastPBlockX, lastPBlockY, 0);}
				savebtn.visible = true;
				itemBackBtn.visible = true;
				itemBtn.visible = true;
				typeBtn.visible = true;
				Menu.visible = false;
				resetbtn.visible = true;
				sharebtn.visible = true;
				closeMenu.visible = true;
				ItemDisplay.visible = true;
				pad.active = false;
				pad.visible = false;
				backbtn.visible = true;
				switchPlayer.visible = true;

		}

		private void onChangePlayer()
		{
				whichPlayer = !whichPlayer;
				if (whichPlayer)
				{
						FlxG.camera.follow(player);
						player.active = true;
						player.visible = true;
						playerLE.active = false;
						playerLE.visible = false;	
				}
				else
				{
						FlxG.camera.follow(playerLE);
						player.active = false;
						player.visible = false;
						playerLE.active = true;
						playerLE.visible = true;	
				}
		}

		private void onCloseMenu()
		{
				pad.visible = true;
				pad.active = true;
				savebtn.visible = false;
				itemBackBtn.visible = false;
				itemBtn.visible = false;
				typeBtn.visible = false;
				Menu.visible = true;	
				closeMenu.visible = false;
				ItemDisplay.visible = false;
				backbtn.visible = false;
				resetbtn.visible = false;
				sharebtn.visible = false;
				switchPlayer.visible = false;

		}

		@Override
		public void destroy()
		{
				super.destroy();
				pause = null;
				level = null;
				block = null;
				playerLE = null;
				pad = null;
		}	
}
