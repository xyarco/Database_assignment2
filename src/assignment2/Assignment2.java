package assignment2;

import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import oracle.spatial.geometry.JGeometry;
import oracle.sql.STRUCT;

class MainFrame extends JFrame{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	JLabel LogoLabel;
	JLabel headLabel;
	JButton notifyButton;
	LoginPanel loginPanel;
	SignupPanel signUpPanel;
	SqlPanel sqlPanel;
	ButtonPanel buttonPanel;
//	PostandSearchPanel postandsearch;
	ResultPanel resultPanel;
	Connection conn=null;
	ArrayList<String> requester = new ArrayList<String>();
	ArrayList<String> Relation= new ArrayList<String>();
	int countrequest=0;
	JTextArea resultArea = null;
	JScrollPane scrollPane = null;
	int trigger = 0;
	JLabel showLabel;
	int hasRequest = 0;
	StringBuffer SQLOut = new StringBuffer ();
	MainFrame(){
		setResizable(false);
		setLayout(null);
		setSize(1100, 700);
		int width=Toolkit.getDefaultToolkit().getScreenSize().width;
		int height=Toolkit.getDefaultToolkit().getScreenSize().height;
		setLocation((width-1100)/2,(height-700)/2);
		setTitle("This is GUI for database homework");
		SetLogo();
		setLoginPanel();
		setSignupPanel();
		setSqlPanel();
		setButtonPanel();
		setResultPanel();
	//	postandsearch.disablePanel();
		buttonPanel.disablePanel();
		
		conn=ConnectDB.openConnection();
	}

	public void disableResult(){
    	resultArea.setText("");
    	resultArea.setEditable(false);
    	resultArea.setEnabled(false);
    	scrollPane.setEnabled(false);
	}

	public void setResultPanel(){

		resultArea = new JTextArea(10,30);
		resultArea.setLineWrap(true);
		scrollPane = new JScrollPane(resultArea);
		headLabel = new JLabel("Shopping System");
		add(scrollPane);
		add(headLabel);
		
		headLabel.setFont(new Font("Serif", Font.BOLD, 30));
		headLabel.setBounds(240,20 , 360, 60);
		scrollPane.setBounds(20, 100,740, 250);
	}
	
	public void setResultOutPut(StringBuffer sb){
		resultArea.setText(sb.toString());
		resultArea.setEnabled(true);
	}

	public void SetLogo(){
	Image image;
	try {
		image = ImageIO.read(new File("usc_viterbi_logo.jpg"));
		ImageIcon icon = new ImageIcon(image);
		LogoLabel = new JLabel();
		LogoLabel.setIcon(icon);
		LogoLabel.setBounds(830,500,300,150);

		add(LogoLabel);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}  //this generates an image file

	}

	

	public void setButtonPanel(){
		buttonPanel = new ButtonPanel();
		buttonPanel.setBounds(30, 380, 700, 90);
		this.add(buttonPanel);

		
		buttonPanel.buttons[0].addActionListener(new ActionListener() {
	          
            public void actionPerformed(ActionEvent e) {
            	StringBuffer result= new StringBuffer();
            	/*Fill this function*/
            	/*Press this your account button, you should be able to list*/
            	/* current log in customer information in the result panel (Including Email, First Name, Last Name, Address)*/
            	/*You can define the output format*/
            	
            	SQLOut.setLength(0);
            	Statement stmt = null;
            	ResultSet re1 = null;
            	ResultSet re2 = null;
            	String sql;
            	
            	String userName = loginPanel.username.getText();
            	try {
					stmt = conn.createStatement();
					sql = "SELECT EMAIL, FNAME, LNAME, ADDRESS_ID FROM MEMBER WHERE EMAIL = '"
							+ userName + "'";
					re1 = stmt.executeQuery(sql);
					SQLOut.append(sql);
					setSQLOutput(SQLOut);
					
					if(re1.next()){
						//get email, fname, lname
						String email = "Email:" + re1.getString(1) + "\n";
						String fname = "Family Name: " + re1.getString(2) + "\n";
						String lname = "Last Name: " + re1.getString(3) + "\n";
						String addrId = re1.getString(4);
						
						sql = "SELECT A.APTNUM, A.STREET_ADDRESS, Z.CITY, Z.STATE, "
								+ "A.ZIPCODE, A.MOBILEPHONENUM FROM ADDRESS A, ZIPCODE Z "
								+ "WHERE A.ADDRID = '" + addrId + "' AND A.ZIPCODE = Z.ZIPCODE";
						re2 = stmt.executeQuery(sql);
						SQLOut.append("\n" +sql);
						setSQLOutput(SQLOut);
						
						if(re2.next()){
							//get address
							String aptnum = re2.getString(1);
							String str_add = re2.getString(2);
							String city = re2.getString(3);
							String state = re2.getString(4);
							String zipcode = re2.getString(5);
							String address = "Address: " + aptnum + " " + str_add + ", " + city + ", " + state 
									+ ", " + zipcode + "\n"; 
							String mobilePhone = "Mobile Phone: " + re2.getString(6) + "\n";
							result.append(email + fname + lname + address + mobilePhone);
						}
						
						setResultOutPut(result);
					}
					else{
						JOptionPane.showMessageDialog(null, "Check your username and password!");
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            	
            	try {
					re2.close();
					re1.close();
					stmt.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });
		
		buttonPanel.buttons[1].addActionListener(new ActionListener() {
          
            public void actionPerformed(ActionEvent e) {
            	StringBuffer result= new StringBuffer();
            	/*Fill this function*/
            	/*Press this all products button, you should be able to list all the products which are visible to you*/
            	/*You can define the output format*/
            	SQLOut.setLength(0);
            	Statement stmt = null;
            	ResultSet re = null;
            	String sql;
            	
            	try {
					stmt = conn.createStatement();
					sql = "SELECT PRODUCTID, CATEGORY, BRAND, NAME, PRICE FROM PRODUCT";
					re = stmt.executeQuery(sql);
					SQLOut.append(sql);
					setSQLOutput(SQLOut);
					
					while(re.next()){
						String proID = "ProductID: " + re.getString(1) +"\n";
						String catg = "Category: " + re.getString(2) + "\n";
						String brand = "Brand: " + re.getString(3) + "\n";
						String name = "Name: " + re.getString(4) + "\n";
						String price = "Price: $" + re.getString(5) + "\n\n";
						result.append(proID + catg + brand + name + price);
					}
					setResultOutPut(result);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            	try {
					re.close();
					stmt.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });

		buttonPanel.buttons[2].addActionListener(new ActionListener() {
          
            public void actionPerformed(ActionEvent e) {
            	final Frame0 frame=new Frame0();
                frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                frame.setVisible(true);
                frame.btn1.addActionListener(new ActionListener() {
                 
                    public void actionPerformed(ActionEvent e) {
                    	/*Fill this function*/
    	            	/*Press this choose category Button, after choosing and pressing OK*/
    	            	/* you should be able to list all products belong to this category*/
                    	
                    	StringBuffer result= new StringBuffer();
                    	SQLOut.setLength(0);
                    	Statement stmt = null;
                    	ResultSet re = null;
                    	String sql;
                    	
                    	String selItem = frame.combo.getSelectedItem().toString();
                    	try {
							stmt = conn.createStatement();
							sql = "SELECT PRODUCTID, CATEGORY, BRAND, NAME, PRICE FROM PRODUCT "
									+ "WHERE CATEGORY = '" + selItem + "'";
							re = stmt.executeQuery(sql);
							SQLOut.append(sql);
							setSQLOutput(SQLOut);
							
							while(re.next()){
								String proID = "ProductID: " + re.getString(1) +"\n";
								String catg = "Category: " + re.getString(2) + "\n";
								String brand = "Brand: " + re.getString(3) + "\n";
								String name = "Name: " + re.getString(4) + "\n";
								String price = "Price: $" + re.getString(5) + "\n\n";
								result.append(proID + catg + brand + name + price);
							}
							setResultOutPut(result);
							
							frame.setVisible(false);
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                    	
                    	try {
                    		re.close();
							stmt.close();	
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                    }
                });
            }
        });

		
		buttonPanel.buttons[3].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
	        	final Frame1 frame=new Frame1("Please input Price Range ");
	            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	            frame.setVisible(true);

	            frame.btn1.addActionListener(new ActionListener() {
	               
	                public void actionPerformed(ActionEvent e) {
	                	/*Fill this function*/
	                	/*Press this set price range Button, you should be able to set price range*/
	                	/*Pressing "Set Price Range" button, a new window will pop out. */
	                	/*Then you can enter "Min_Price" & "Max_Price" and press "Search" button, */
	                	/*and then all products belong to category that you choose should be shown in the result panel.*/
                    	StringBuffer result= new StringBuffer();
                    	SQLOut.setLength(0);
                    	Statement stmt = null;
                    	ResultSet re = null;
                    	String sql;
                    	
                    	float min = Float.valueOf(frame.txtfield[0].getText());
                    	float max = Float.valueOf(frame.txtfield[1].getText());
                    	
                    	//check input
                    	if(max >= min && max > 0){
                    		try {
								stmt = conn.createStatement();
								//check result
							    sql = "SELECT COUNT(*) FROM PRODUCT WHERE PRICE >= " + min 
									    + "AND PRICE <= " + max ;
							    re = stmt.executeQuery(sql);
							    SQLOut.append(sql);
							    setSQLOutput(SQLOut);
							    
							    if(re.next()){
							    	int count = re.getInt(1);
							    	if(count != 0){
							    		sql = "SELECT PRODUCTID, CATEGORY, BRAND, NAME, PRICE FROM PRODUCT "
								                + "WHERE PRICE >= " + min + " AND PRICE <= " + max + " ORDER  BY PRICE";
								        re = stmt.executeQuery(sql);
								        SQLOut.append("\n" + sql);
								        setSQLOutput(SQLOut);
								        
								        while(re.next()){
								        	String proID = "ProductID: " + re.getString(1) +"\n";
									        String catg = "Category: " + re.getString(2) + "\n";
									        String brand = "Brand: " + re.getString(3) + "\n";
									        String name = "Name: " + re.getString(4) + "\n";
									        String price = "Price: $" + re.getString(5) + "\n\n";
									        result.append(proID + catg + brand + name + price);
								        }
								        setResultOutPut(result);
							    	}else{
							    		result.append("No result!");
									    setResultOutPut(result);
							    	}
							    	frame.setVisible(false);
							    }
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
                    		
                    		try {
								re.close();
								stmt.close();
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
                    	} else{
                    		JOptionPane.showMessageDialog(null, "Please check your input!");
                    		}
                    	}
	                });
	       
			}
			
		});
            
     

		buttonPanel.buttons[4].addActionListener(new ActionListener() {
           
        public void actionPerformed(ActionEvent e) {
        	final Frame2 frame=new Frame2();
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            frame.setVisible(true);

            frame.btn1.addActionListener(new ActionListener() {
               
                public void actionPerformed(ActionEvent e) {
                	/*Fill this function*/     
//                	Press "Order Products" button, a new window will pop out. 
//                	Then you can enter "Product ID" and "Quantity", and then you press continue, 
//                	the Total Price should be shown correctly. 
//                	Then, you can press "Place Order" to complete this order. 
//                	This new order should be synchronized in the database.

                	SQLOut.setLength(0);
                	Statement stmt = null;
                	ResultSet re = null;
                	String sql;
                	
                	String proID = frame.productID.getText();
                	String qa = frame.quantity.getText();
                	
                	//check input
                	if(qa.matches("[0-9]*") && !proID.isEmpty()){
                		int quantity = Integer.valueOf(frame.quantity.getText());
                		try {
							stmt = conn.createStatement();
							//check productID
							sql = "SELECT COUNT(*) FROM PRODUCT WHERE PRODUCTID = '"
									+ proID + "'";
							re = stmt.executeQuery(sql);
							SQLOut.append(sql);
							setSQLOutput(SQLOut);
							
							if(re.next()){
								if(re.getInt(1) != 0){
									sql = "SELECT PRICE FROM PRODUCT WHERE PRODUCTID = '"
								            + proID + "'";
									re = stmt.executeQuery(sql);
									SQLOut.append("\n" + sql);
									setSQLOutput(SQLOut);
									
									while(re.next()){
										float price = Float.valueOf(re.getString(1));
										float totalPrice = price * quantity;
										frame.totalPrice.setText(String.valueOf(totalPrice));
									}
								}else{
									JOptionPane.showMessageDialog(null, "Wrong ProductID.");
								}
								
							}
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                		
                		try {
							re.close();
							stmt.close();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                	}else{
                		JOptionPane.showMessageDialog(null, "Please check your input!");
                	}

                }
            });


            frame.btn2.addActionListener(new ActionListener() {
           
                public void actionPerformed(ActionEvent e) {
                	/*Fill this function*/
                	/*Press this accept all Button, you should be able to accept all friend request and add this information into friend relationship table*/
                	/*pop up a standard dialog box to show <succeed or failed>*/
                	
                	SQLOut.setLength(0);
                	StringBuffer result = new StringBuffer();
                	Statement stmt = null;
                	ResultSet re = null;
                	String sql;
                	
                	String orderID;
                	SimpleDateFormat date = new SimpleDateFormat("MM/dd/yyyy");
                    String str_date = date.format(new Date());
                	String customer = loginPanel.username.getText();
                	String proID = frame.productID.getText();
                	String qa = frame.quantity.getText();
                	
                	if(qa.matches("[0-9]*") && !proID.isEmpty()){
                		int quantity = Integer.valueOf(frame.quantity.getText());
                		try {
							stmt = conn.createStatement();
							//check productID
							sql = "SELECT COUNT(*) FROM PRODUCT WHERE PRODUCTID = '"
									+ proID + "'";
							re = stmt.executeQuery(sql);
							SQLOut.append(sql);
							setSQLOutput(SQLOut);
							
							if(re.next()){
								if(re.getInt(1) == 0){
									JOptionPane.showMessageDialog(null, "Wrong ProductID.");
								}else{
									//calculate orderID
									sql = "SELECT COUNT(*) FROM ORDERS";
									re = stmt.executeQuery(sql);
									SQLOut.append("\n" + sql);
									setSQLOutput(SQLOut);
									
									if(re.next()){
										int orderNum = re.getInt(1) + 1;
										orderID = "O" + orderNum;
										
										//insert orders
										sql = "INSERT INTO ORDERS(ORDERID, CUSTOMER, PLACETIME) VALUES('"
												+ orderID + "','" + customer + "',to_date('" + str_date 
												+ "','MM/DD/YYYY'))";
										re = stmt.executeQuery("\n" + sql);
										SQLOut.append("\n" + sql);
										setSQLOutput(SQLOut);
										
										if(re.next()){
											//insert order_product
											sql = "INSERT INTO ORDER_PRODUCT(ORDERID, PRODUCTID, QUANTITY) VALUES('"
													+ orderID + "','" + proID + "','" + quantity + "')";
											re = stmt.executeQuery("\n" + sql);
											SQLOut.append("\n" + sql);
											setSQLOutput(SQLOut);
											
											if(re.next()){
												frame.setVisible(false);
												JOptionPane.showMessageDialog(null, "Success!");
												result.append("You have ordered " + quantity + " " + proID + ".");
												setResultOutPut(result);
											}
										}
									}
								}
							}
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                		
                		try {
							re.close();
							stmt.close();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                	}else{
                		JOptionPane.showMessageDialog(null, "Please check your input!");
                	}
                	
                }
            });

        }
		});

		buttonPanel.buttons[5].addActionListener(new ActionListener() {
	          
            public void actionPerformed(ActionEvent e) {
            	StringBuffer result= new StringBuffer();
            	/*Fill this function*/
            	/*Press "Your Orders", all order history of this customer should be shown in the result panel.*/
            	
            	SQLOut.setLength(0);
            	Statement stmt = null;
            	Statement stmt1 = null;
            	ResultSet re = null;
            	ResultSet re1 = null;
            	String sql;
            	String sql1;
            	
            	String customer = loginPanel.username.getText();
            	SimpleDateFormat date = new SimpleDateFormat("MM/dd/yyyy");
            	String placeTime;
            	String shipTime;
            	String estArrTime;
            	String signedTime;
            	String trackNum;
            	
            	try {
					stmt = conn.createStatement();
					stmt1 = conn.createStatement();
					//get order
					sql = "SELECT * FROM ORDERS WHERE CUSTOMER = '" + customer + "'";
					re = stmt.executeQuery(sql);
					SQLOut.append(sql);
					setSQLOutput(SQLOut);
					
					while(re.next()){
						String orderID = "OrderID: " + re.getString(1) + "\n";
						String totalPrice = "Totoal Price: " + re.getString(3) + "\n";
						trackNum = "Tracking number: " + re.getString(8) + "\n\n";
						
						Date pTime = re.getDate(4);
						Date sTime = re.getDate(5);
						Date eaTime = re.getDate(6);
						Date siTime = re.getDate(7);

						if(pTime == null){
							placeTime = "Place Time: null.\n";
						}else{
							placeTime = "Place Time: " + date.format(pTime) + "\n";
						}
						
						if(sTime == null){
							shipTime = "Shipped Time: null.\n";
						}else{
							shipTime = "Shipped Time: " + date.format(sTime) + "\n";
						}
						
						if(eaTime == null){
							estArrTime = "Estimated Arrival Time: null. \n";
						}else{
							estArrTime = "Estimated Arrival Time: " + date.format(eaTime) + "\n";
						}
						
						if(siTime == null){
							signedTime = "Signed Time: null. \n";
						}else{
							signedTime = "Signed Time: " + date.format(siTime) + "\n";
						}
						
						result.append(orderID);
						
						//get product and quantity in each order
						String orderID1 = re.getString(1);
						sql1 = "SELECT PRODUCTID, QUANTITY FROM ORDER_PRODUCT WHERE ORDERID = '" + orderID1 + "'";
						re1 = stmt1.executeQuery(sql1);
						SQLOut.append("\n" + sql1);
						setSQLOutput(SQLOut);
						
						while(re1.next()){
							String proID = "Product: " + re1.getString(1) + "\n";
							String quantity = "Quantity: " + re1.getString(2) + "\n";
							result.append(proID + quantity);
						}
						
						result.append(totalPrice + placeTime + shipTime + estArrTime + signedTime + trackNum);
					}
					setResultOutPut(result);
					
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            	
            	try {
            		re1.close();
					re.close();
					stmt1.close();
					stmt.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });

		
		buttonPanel.buttons[6].addActionListener(new ActionListener() {
	           
	        public void actionPerformed(ActionEvent e) {
	        	final Frame5 frame=new Frame5("Product ID : ","Review : ");
	            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	            frame.setVisible(true);

	            frame.btn1.addActionListener(new ActionListener() {
	               
	                public void actionPerformed(ActionEvent e) {
	                	/*Fill this function*/
	                	/*Press "Review Products" button, a new window will pop out. */
	                	/*Input product ID and review content and press the OK button, this information should be inserted into database.*/
	                	
	                	SQLOut.setLength(0);
	                	StringBuffer result = new StringBuffer();
	                	Statement stmt = null;
	                	ResultSet re = null;
	                	String sql;
	                	
	                	String reviewID;
	                	String author = loginPanel.username.getText();
						String proID = frame.txtfield.getText();
						String content = frame.textArea.getText();
						SimpleDateFormat date = new SimpleDateFormat("MM/dd/yyyy");
		                String str_date = date.format(new Date());
		                
		                //check input
		                if(!proID.isEmpty() && !content.isEmpty()){
		                	try {
								stmt = conn.createStatement();
								//check productID
								sql = "SELECT COUNT(*) FROM PRODUCT WHERE PRODUCTID = '"
										+ proID + "'";
								re = stmt.executeQuery(sql);
								SQLOut.append(sql);
								setSQLOutput(SQLOut);
								
								if(re.next()){
									if(re.getInt(1) != 0){
										sql = "SELECT COUNT(*) FROM REVIEW";
										re = stmt.executeQuery(sql);
										SQLOut.append("\n" + sql);
										setSQLOutput(SQLOut);
										
										if(re.next()){
											int reviewNum = re.getInt(1) + 1;
											reviewID = "R" + reviewNum;
											
											sql = "INSERT INTO REVIEW(REVIEWID, POSTED_TIME, CONTENT, "
													+ "AUTHORID, PRODUCTID) VALUES('"
													+ reviewID +"',to_date('"+ str_date + "','MM/DD/YYYY'),'"
															+ content + "', '" + author + "', '" + proID +"')";
											re = stmt.executeQuery(sql);
											SQLOut.append("\n" + sql);
											setSQLOutput(SQLOut);
											
											if(re.next()){
												frame.setVisible(false);
												JOptionPane.showMessageDialog(null, "Success!");
												result.append("Your review \"" + content + "\" on " + proID + " has been added.");
												setResultOutPut(result);
											}
										}
									}else{
										JOptionPane.showMessageDialog(null, "Wrong ProductID!");
									}
								}
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
		                }else{
		                	JOptionPane.showMessageDialog(null, "Please check your input!");
		                }
		                
	                }
	            });

	        }
		});
		buttonPanel.buttons[7].addActionListener(new ActionListener() {
	           
	        public void actionPerformed(ActionEvent e) {
	        	final Frame4 frame=new Frame4("Product ID : ","Submit");
	            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	            frame.setVisible(true);

	            frame.btn1.addActionListener(new ActionListener() {
	               
	                public void actionPerformed(ActionEvent e) {
	                	/*Fill this function*/
	                	/*Press "List All Reviews" button, a new window will pop out. */
	                	/*Input "Product ID" and press submit, all reviews about this product should be shown in the result panel.*/
	                	
	                	SQLOut.setLength(0);
	                	Statement stmt = null;
	                	ResultSet re = null;
	                	StringBuffer result= new StringBuffer();
	                	String sql;
	                	
	                	String proID = frame.txtfield.getText();
	                	
	                	//check field
	                	if(!proID.isEmpty()){
	                		try {
								stmt = conn.createStatement();
								//check input
								sql = "SELECT COUNT(*) FROM PRODUCT WHERE PRODUCTID = '"
										+ proID + "'";
								re = stmt.executeQuery(sql);
								SQLOut.append(sql);
								setSQLOutput(SQLOut);
								
								if(re.next()){
									int count = re.getInt(1);
									if(count != 0){
										result.append("Review(s) on " + proID + "\n\n");
										sql = "SELECT * FROM REVIEW WHERE PRODUCTID = '" + proID + "'";
										re = stmt.executeQuery(sql);
										SQLOut.append("\n" + sql);
										setSQLOutput(SQLOut);
										
										while(re.next()){
											String reviewID = "ReviewID: " + re.getString(1) + "\n";
											String postTime = "Posted Time: " + re.getString(2) + "\n";
											String author = "Author: " + re.getString(5) + "\n";
											String rating = "Rating: " + re.getString(3) + "\n";
											String content = "Content: " + re.getString(4) + "\n\n";
											result.append(reviewID + postTime + author + rating + content);
										}
										setResultOutPut(result);
										frame.setVisible(false);
									}else{
										JOptionPane.showMessageDialog(null, "Wrong productID!");
									}
								}
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
	                		
	                		try {
								re.close();
								stmt.close();
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
	                	}else{
	                		JOptionPane.showMessageDialog(null, "Please check your input!");
	                	}
	                	
	                }
	            });

	        }
		});
		
		buttonPanel.buttons[8].addActionListener(new ActionListener() {
	           
	        public void actionPerformed(ActionEvent e) {
	        	final Frame4 frame=new Frame4("Review ID : ","Like it");
	            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	            frame.setVisible(true);

	            frame.btn1.addActionListener(new ActionListener() {
	               
	                public void actionPerformed(ActionEvent e) {
	                	/*Fill this function*/
	                	/*Press "Like Reviews" button, a new window will pop out. */
	                	/*Input "Review ID" and press "Like it", this information should be inserted into database.*/
	                	SQLOut.setLength(0);
	                	StringBuffer result = new StringBuffer();
	                	Statement stmt = null;
	                	ResultSet re = null;
	                	String sql;
	                	
	                	String reviewID = frame.txtfield.getText();
	                	String email = loginPanel.username.getText();
	                	
	                	//check field
	                	if(!reviewID.isEmpty()){
	                		try {
								stmt = conn.createStatement();
								//check input
								sql = "SELECT COUNT(*) FROM REVIEW WHERE REVIEWID = '" + reviewID + "'";
								re = stmt.executeQuery(sql);
								SQLOut.append(sql);
								setSQLOutput(SQLOut);
								
								if(re.next()){
									int count_re = re.getInt(1);
									if(count_re != 0){
										
										//check if like_review is already exists
										sql = "SELECT COUNT(*) FROM LIKE_REVIEW WHERE REVIEWID = '"
												+ reviewID + "' AND EMAIL = '" + email + "'";
										re = stmt.executeQuery(sql);
										SQLOut.append("\n" + sql);
										setSQLOutput(SQLOut);
										
										if(re.next()){
											int count_like = re.getInt(1);
											if(count_like == 0){
												//insert like_review
												sql = "INSERT INTO LIKE_REVIEW(REVIEWID,EMAIL) VALUES('"
											            + reviewID + "','" + email + "')";
												re = stmt.executeQuery(sql);
												SQLOut.append("\n" + sql);
												setSQLOutput(SQLOut);
												
												if(re.next()){
													frame.setVisible(false);
													JOptionPane.showMessageDialog(null, "Success!");
													result.append("You like review " + reviewID + ".");
													setResultOutPut(result);
												}
											}else{
												frame.setVisible(false);
												JOptionPane.showMessageDialog(null, "You have already liked this review.");
											}
										}
										
									}else{
										JOptionPane.showMessageDialog(null, "Wrong ReviewID!");
									}
								}
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
	                		
	                		try {
								re.close();
								stmt.close();
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
	                	}else{
	                		JOptionPane.showMessageDialog(null, "Please check your input!");
	                	}
	                }
	            });

	        }
		});	
		
		buttonPanel.buttons[9].addActionListener(new ActionListener() {
	          
            public void actionPerformed(ActionEvent e) {
            	StringBuffer result= new StringBuffer();
            	/*Fill this function*/
            	/*Press "List All Likes" button, all reviews that liked by this customer should be shown in the result panel.*/
            	SQLOut.setLength(0);
            	Statement stmt = null;
            	ResultSet re = null;
            	String sql;
            	String email = loginPanel.username.getText();
            	
            	try {
					stmt = conn.createStatement();
					sql = "SELECT * FROM REVIEW WHERE REVIEWID IN "
							+ "(SELECT REVIEWID FROM LIKE_REVIEW WHERE "
							+ "LIKE_REVIEW.EMAIL = '" + email + "')";
					re = stmt.executeQuery(sql);
					SQLOut.append(sql);
					setSQLOutput(SQLOut);
					
					while(re.next()){
						String reviewID = "ReviewID: " + re.getString(1) + "\n";
						String postTime = "Posted Time: " + re.getString(2) + "\n";
						String author = "Author: " + re.getString(5) + "\n";
						String proID = "Product: " + re.getString(6) + "\n";
						String rating = "Rating: " + re.getString(3) + "\n";
						String content = "Content: " + re.getString(4) + "\n\n";
						result.append(reviewID + postTime + author + proID + rating + content);
					}
					setResultOutPut(result);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            	try {
					stmt.close();
					re.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });
		buttonPanel.buttons[10].addActionListener(new ActionListener() {
	          
            public void actionPerformed(ActionEvent e) {
            	StringBuffer result= new StringBuffer();
            	/*Fill this function*/
            	/*Press "Nearest Seller" button, the nearest seller info for this customer should be shown in the result panel.*/
            	/*This is a spatial query*/
            	SQLOut.setLength(0);
            	Statement stmt = null;
            	ResultSet re = null;
            	String sql;
            	String email = loginPanel.username.getText();
            	try {
					stmt = conn.createStatement();
					sql = "SELECT S.EMAIL FROM SELLER S, MEMBER M, ADDRESS A "
							+ "WHERE S.EMAIL = M.EMAIL AND A.ADDRID = M.ADDRESS_ID "
							+ "AND SDO_NN(A.COORDINATE, (SELECT A1.COORDINATE "
							+ "FROM ADDRESS A1, MEMBER M1 WHERE A1.ADDRID = M1.ADDRESS_ID "
							+ "AND M1.EMAIL = '" + email +"'),'SDO_BATCH_SIZE = 1') = 'TRUE' "
							+ "AND S.EMAIL != '" + email +"' AND ROWNUM = 1";
					re = stmt.executeQuery(sql);
					SQLOut.append(sql);
					setSQLOutput(SQLOut);
					
					if(re.next()){
						String nearSeller = "The nearest seller is " + re.getString(1);
						result.append(nearSeller);
					}
					setResultOutPut(result);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            	try {
					stmt.close();
					re.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });
		
		buttonPanel.buttons[11].addActionListener(new ActionListener() {
           
            public void actionPerformed(ActionEvent e) {
            	final Frame3 frame=new Frame3("Please input coordinate: ");
                frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                frame.setVisible(true);

                frame.btn1.addActionListener(new ActionListener() {
                  
                    public void actionPerformed(ActionEvent e) {
                    	/*Fill this function*/
                    	/*Press this Button, input left top corner coordinate and right down corner coordinate*/
                    	/*press ok, you should be able list the information(including address information) about seller who lives in this area. Close query window*/
                    	/*This is a spatial query*/
                    	StringBuffer result = new StringBuffer();
                    	
                    	SQLOut.setLength(0);
                    	Statement stmt = null;
                    	ResultSet re = null;
                    	String sql;
                    	
                    	String topLeft = frame.txtfield[0].getText();
                    	String topRt = frame.txtfield[1].getText();
                    	String bottomLt = frame.txtfield[2].getText();
                    	String bottomRt = frame.txtfield[3].getText();
                    	
                    	//check input
                    	String regex = "^[-]?[0-9]+(\\.\\d+)?$";
                    	if(topLeft.matches(regex) && topRt.matches(regex) && bottomLt.matches(regex) && bottomRt.matches(regex)){
                    		try {
								stmt = conn.createStatement();
								sql = "SELECT C.EMAIL FROM CUSTOMER C, MEMBER M, ADDRESS A "
										+ "WHERE M.EMAIL = C.EMAIL AND M.ADDRESS_ID = A.ADDRID "
										+ "AND SDO_INSIDE(A.COORDINATE, SDO_GEOMETRY(2003, NULL, NULL, "
										+ "SDO_ELEM_INFO_ARRAY(1,1003,3), SDO_ORDINATE_ARRAY("
										+ topLeft + "," + topRt + "," + bottomLt + "," + bottomRt
										+ "))) = 'TRUE'";
								re = stmt.executeQuery(sql);
								SQLOut.append(sql);
								setSQLOutput(SQLOut);
								
								while(re.next()){
									String customer = re.getString(1) + "\n";
									result.append(customer);
								}
								setResultOutPut(result);
								
								frame.setVisible(false);
								frame.txtfield[0].setText("");
								frame.txtfield[1].setText("");
								frame.txtfield[2].setText("");
								frame.txtfield[3].setText("");
								
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
                    		
                    		try {
								re.close();
								stmt.close();
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
                    	}else{
                    		JOptionPane.showMessageDialog(null, "Please check your input!");
                    	}
                    }
                });
            }
        });
		
	}

	
	public void setSQLOutput(StringBuffer sb)
	{
		sqlPanel.SQLArea.setText(sb.toString());
		sqlPanel.SQLArea.setEnabled(true);
	}
	public void setSqlPanel(){
		sqlPanel = new SqlPanel();
		showLabel = new JLabel("The corresponding SQL sentence:");
		showLabel.setBounds(30, 490, 400, 20);
		sqlPanel.setBounds(5, 515,790, 150);
		this.add(sqlPanel);
		this.add(showLabel);
	}

	public void setLoginPanel(){
		loginPanel = new LoginPanel();
		this.add(loginPanel);

		loginPanel.signup.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
		           signUpPanel.enablePanel();
			}
        });
        loginPanel.login.addActionListener(new ActionListener() {
           
            public void actionPerformed(ActionEvent e) {  
            //	buttonPanel.enablePanel();
            	/*Fill this function*/
            	/*Press this Button, you should be able match the user information. If valid, keep the user email information(but can't modified) and clear the password*/
            	/*If invalid, you should pop up a dialog box to notify user, then enable signup panel for user to register*/
            	/*After logged in, you should change this button's function as logout which means disable all the panel, return to the original state*/
            	SQLOut.setLength(0);
            	if(trigger==0){
               	 //match account
               	 String QueryStr = "SELECT EMAIL FROM MEMBER WHERE Email='"+loginPanel.username.getText()+"' AND PASSWORD = '"+loginPanel.password.getText()+"'";
               	 SQLOut.append(QueryStr+"\n\n");
               	 try {
   					Statement stmt = conn.createStatement();
   					ResultSet re = stmt.executeQuery(QueryStr);
   					 if(re.next())
   	            	 {
   	            		 loginPanel.setUserName(loginPanel.username.getText().toString());
   	            		 loginPanel.disablePanel();
   	            		 loginPanel.password.setText("");
   	            		 trigger = 1;
   	            		 loginPanel.login.setText("logout");
   	            		 signUpPanel.disablePanel();
   	            		 buttonPanel.enablePanel();
  	            		 loginPanel.signup.setEnabled(false);

   	            	 }
   	            	 else
   	            	 {
  	            		 JOptionPane.showMessageDialog(null, "No ... please signup");
   	            		 signUpPanel.enablePanel();
   	            		// loginPanel.disablePanel();
   	            	}
   					 //ConnectDB.closeConnection(conn);

   				} catch (SQLException e1) {
   					// TODO Auto-generated catch block
   					e1.printStackTrace();
   					ConnectDB.closeConnection(conn);
   					return;
   				}

               	//getnotification


               }else{
               	loginPanel.login.setText("login");
               	loginPanel.enablePanel();
               	loginPanel.signup.setEnabled(true);
               	loginPanel.password.setText("");
               	loginPanel.username.setText("");
               	disableResult();
               	trigger = 0;
               	buttonPanel.disablePanel();
           	}
               	setSQLOutput(SQLOut);
              	}

           });
   

	}

	public void setSignupPanel(){

		signUpPanel = new SignupPanel();
		this.add(signUpPanel);
		signUpPanel.signup.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {  
            	/*Fill this function*/
            	/*Press this signup button, you should be able check whether current account is existed. If existed, pop up an error, if not check input validation(You can design this part according to your database table's restriction) create the new account information*/
            	/*pop up a standard dialog box to show <succeed or failed>*/
            	SQLOut.setLength(0);
            	
            	String email = signUpPanel.email.getText();
            	String passWord = signUpPanel.password.getText();
            	String rePassWord = signUpPanel.password2.getText();
            	String fName = signUpPanel.fname.getText();
            	String lName = signUpPanel.lname.getText();
            	String birthDate = signUpPanel.birthday.getText();
            	String city = signUpPanel.city.getText();
            	String strAddress = signUpPanel.str_address.getText();
            	String strNo = signUpPanel.str_no.getText();
            	String state = signUpPanel.state.getText();
            	String zip = signUpPanel.zip.getText();
            	String addrID = null;
            	
            	Statement stmt = null;
            	Statement stmt1 = null;
            	ResultSet re = null;
            	ResultSet re1 = null;
            	String sql;
            	String sql1;
            	
            	//check textFields
            	String email_reg = "^[a-zA-Z0-9][\\w\\-\\.]{1,29}@([a-zA-Z]+[\\w\\-]*)(\\.[a-z]{2,4})*$";
            	String birth_reg = "^\\d{2}/\\d{2}/\\d{4}";
            	String strNo_reg = "^\\d+$";
            	String zip_reg = "\\d{5}$";
            	
            	if(email.matches(email_reg) &&
            			passWord.length() > 0 && passWord.length() <=20 &&
            			passWord.equals(rePassWord) &&
            			fName.length() > 0 && fName.length() <=40 &&
            			lName.length() > 0 && lName.length() <= 40 &&
            			birthDate.matches(birth_reg) &&
            			city.length() > 0 && city.length() <= 40 &&
            			state.length() >0 && state.length() <=20 &&
            			strAddress.length() >0 && strAddress.length() <=100 &&
            			strNo.matches(strNo_reg) && strNo.length() <= 20 &&
            			zip.matches(zip_reg)){
            		
            		try {
            			stmt = conn.createStatement();
            			stmt1 = conn.createStatement();
            			//check whether user exists
            			sql = "SELECT COUNT(*) FROM MEMBER WHERE EMAIL = '" + email + "'";
						re = stmt.executeQuery(sql);
						SQLOut.append(sql);
						setSQLOutput(SQLOut);
						
						if(re.next()){
							int count_user = re.getInt(1);
							if(count_user == 0){
								//check if address already exists
								sql1 = "SELECT COUNT(*) FROM ADDRESS WHERE APTNUM = '" + strNo +
										"' AND STREET_ADDRESS = '" + strAddress + "' AND ZIPCODE = '" +
										zip + "'";
								re1 = stmt1.executeQuery(sql1);
								SQLOut.append("\n" + sql1);
								setSQLOutput(SQLOut);
								
								if(re1.next()){
									int count_add = re1.getInt(1);
									if(count_add != 0){
										sql1 = "SELECT ADDRID FROM ADDRESS WHERE APTNUM = '" + strNo +
										"' AND STREET_ADDRESS = '" + strAddress + "' AND ZIPCODE = '" +
										zip + "'";
										re1 = stmt1.executeQuery(sql1);
										SQLOut.append("\n" + sql1);
										setSQLOutput(SQLOut);
										
										if(re1.next()){
											addrID = re1.getString(1);
										}
									}else{
										sql1 = "SELECT COUNT(*) AS ADD_NUM FROM ADDRESS";
										re1 = stmt1.executeQuery(sql1);
										SQLOut.append("\n" + sql1);
										setSQLOutput(SQLOut);
										
										if(re1.next()){
											int num = re1.getInt(1) + 1;
											addrID = "A" + String.valueOf(num);
											
											//if zipcode exists
											sql1 = "SELECT COUNT(*) FROM ZIPCODE WHERE ZIPCODE = '" + zip + "'";
											re1 = stmt1.executeQuery(sql1);
											SQLOut.append("\n" + sql1);
											setSQLOutput(SQLOut);
											
											if(re1.next()){
												int zip_count = re1.getInt(1);
												if(zip_count == 0){
													//insert into zipcode
													sql1 = "INSERT INTO ZIPCODE(ZIPCODE, CITY, STATE) VALUES('"
															+ zip + "','" + city + "','" + state + "')";
													re1 = stmt1.executeQuery(sql1);
													SQLOut.append("\n" + sql1);
													setSQLOutput(SQLOut);
													
													if(re1.next()){
														System.out.print("zipcode insert.");
													}
												}

												//insert into address
												sql1 = "INSERT INTO ADDRESS(ADDRID, APTNUM, STREET_ADDRESS,"
														+ "ZIPCODE) VALUES('"+ addrID + "','" + strNo + "','" 
														+ strAddress + "','" + zip + "')";
												re1 = stmt1.executeQuery(sql1);
												SQLOut.append("\n" + sql1);
												setSQLOutput(SQLOut);
												if(re1.next()){
													System.out.print("Insert Address.");
												}
											}
										}
										
										re1.close();
									}
									
									//insert member
									sql = "INSERT INTO MEMBER(EMAIL, PASSWORD, FNAME, LNAME,"
											+ "BIRTHDATE, ADDRESS_ID) VALUES('" + email + "','"
											+ passWord + "','" + fName + "','" + lName + "', to_date('" +birthDate +
											"','DD/MM/YYYY'),'" + addrID +"')";
									re = stmt.executeQuery(sql);
									SQLOut.append("\n" + sql);
									setSQLOutput(SQLOut);
									
									if(re.next()){
										//insert customer
										sql = "INSERT INTO CUSTOMER(EMAIL) VALUES('" + email + "')";
										re = stmt.executeQuery(sql);
										SQLOut.append("\n" + sql);
										setSQLOutput(SQLOut);
										
										if(re.next()){
											JOptionPane.showMessageDialog(null, "Success");
											signUpPanel.disablePanel();
										}
									}
								}
							}else{
								JOptionPane.showMessageDialog(null, "User Exists. Please login.");
							}
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
            		
            		try {
						re.close();
						stmt1.close();
						stmt.close();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
            		
            	}
            	else if(!email.matches(email_reg)){
            		JOptionPane.showMessageDialog(null, "Please enter a legal email.");
            	}else if(passWord.length() <= 0 || passWord.length() >20){
            		JOptionPane.showMessageDialog(null, "Password length should be 1-20.");
            	}else if(!passWord.equals(rePassWord)){
            		JOptionPane.showMessageDialog(null, "Please reenter the password correctly.");
            	}else if(fName.length() <= 0 || fName.length() > 40 || lName.length() <= 0 || lName.length() > 40 ){
            		JOptionPane.showMessageDialog(null, "FName(LName) length shoule be 1-40");
            	}else if(!birthDate.matches(birth_reg)){
            		JOptionPane.showMessageDialog(null, "The form of Birthday should be \"MM/DD/YYYY\". \n Example: 03/26/1995");
            	}else if(!strNo.matches(strNo_reg) || strNo.length() > 20){
            		JOptionPane.showMessageDialog(null, "StrNo should be a number and the length should be 1-20.");
            	}else if(strAddress.length() <= 0 || strAddress.length() > 100){
            		JOptionPane.showMessageDialog(null, "Street Address length should be 1-100.");
            	}else if(city.length() <= 0 || city.length() > 40){
            		JOptionPane.showMessageDialog(null, "City length should be 1-40.");
            	}else if(state.length() <= 0 || state.length() > 20){
            		JOptionPane.showMessageDialog(null, "State length should be 1-20.");
            	}else if(!zip.matches(zip_reg)){
            		JOptionPane.showMessageDialog(null, "Zipcode should be a 5-digit number. \n Example: 90007");
            	}else {
            		JOptionPane.showMessageDialog(null, "Please double check each field.");
            	}
            	
            }
        });

		signUpPanel.disablePanel();

	}


}


class ConnectDB{

	public static Connection openConnection(){
        try{
	        String driverName = "oracle.jdbc.driver.OracleDriver";
	        Class.forName(driverName);

	        //set the username and password for your connection.
	        String url = "jdbc:oracle:thin:@localhost:1521:orcl";
	        String uname = "SCOTT";
	        String pwd = "123456";

	        return DriverManager.getConnection(url, uname, pwd);
        }
        catch(ClassNotFoundException e){
        	System.out.println("Class Not Found");
        	e.printStackTrace();
        	return null;
        }
        catch(SQLException sqle){
        	System.out.println("Connection Failed");
        	sqle.printStackTrace();
        	return null;
        }

	}
	public static void closeConnection(Connection conn)
	{
		try{
		 conn.close();
	 }
	    catch (Exception e){
	    	e.printStackTrace();
	    	System.out.println("connection closing error ");
	    }
	}
}
public class Assignment2 {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
    	MainFrame frame = new MainFrame();
    	frame.setVisible(true);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
