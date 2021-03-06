import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import com.sun.glass.ui.Window;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import net.proteanit.sql.DbUtils;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.SwingConstants;

public class Ausleihfenster extends JFrame {

	private JPanel contentPane;
	private JTextField kundensucheTextField;
	private JButton suchenButton;
	private JPanel kundenlistePanel;
	private JLabel kundenlisteLabel;
	private JTable kundenlisteTable;
	private JScrollPane scrollPane;
	private JButton kundenAnlegenButton;
	private JLabel leihdauerInTagenLabel;
	private JTextField leihdauerInTagenTextField;
	private JLabel ausleihpreisLabel;
	private JTextField ausleihpreisTextField;
	private double ausleihpreisProTag;
	private double gesamtausleihpreis;
	private JLabel ausleihmengeLabel;
	private JTextField ausleihmengeTextField;
	private JButton preisBerechnenButton;
	private String spiel;
	Ausleihfenster fenster;
	
	SpielDAO spielDAO = new SpielDAO();
	//Spiel spiel = new Spiel();
	KundenDAO kundenDAO = new KundenDAO();
	GetWertInZeile kundeAuswaehlen = new GetWertInZeile();
	KundenSpieleDAO kundenSpieleDAO = new KundenSpieleDAO();
		
	String pattern = "#0.00";
	DecimalFormat df = new DecimalFormat(pattern);
	private JButton ausleihenButton;
	private JTextField waehrungTextField;

	/**
	 * Create the frame.
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	
	public Ausleihfenster(String spiel) throws ClassNotFoundException, SQLException  {
		this.spiel = spiel;
		initGUI();
	}
	private void initGUI() {
		setTitle("Ausleihfenster");
		setBounds(100, 100, 600, 400);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(this.contentPane);
		contentPane.setLayout(null);
		{
			this.kundensucheTextField = new JTextField();
			this.kundensucheTextField.setToolTipText("Hier kann ein Kundennachname eingegeben werden");
			kundensucheTextField.setBounds(155, 11, 320, 20);
			this.contentPane.add(this.kundensucheTextField);
			this.kundensucheTextField.setColumns(10);
		}
		{
			this.suchenButton = new JButton("Suchen");
			this.suchenButton.setToolTipText("Bei Klick auf den Button wird nach einem Kunden gesucht");
			suchenButton.setBounds(485, 10, 89, 23);
			this.suchenButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						do_suchenButton_actionPerformed(e);
					} catch (ClassNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			this.contentPane.add(this.suchenButton);
		}
		{
			this.kundenlistePanel = new JPanel();
			kundenlistePanel.setBounds(10, 61, 284, 290);
			this.kundenlistePanel.setLayout(null);
			this.kundenlistePanel.setBorder(new LineBorder(new Color(0, 0, 0)));
			this.contentPane.add(this.kundenlistePanel);
			{
				this.kundenlisteTable = new JTable();
				this.kundenlisteTable.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						do_kundenlisteTable_mouseClicked(e);
					}
				});
				this.kundenlisteTable.setModel(new DefaultTableModel(
					new Object[][] {
					},
					new String[] {
						"ID", "Vorname", "Nachname", "IBAN", "Strasse"
					}
				));
				this.kundenlisteTable.getColumnModel().getColumn(1).setResizable(false);
				this.kundenlisteTable.getColumnModel().getColumn(2).setResizable(false);
				this.kundenlisteTable.setBounds(10, 11, 264, 268);
				this.kundenlistePanel.add(this.kundenlisteTable);
				this.kundenlisteTable.removeEditor();
			}
			{
				this.scrollPane = new JScrollPane(kundenlisteTable);
				this.scrollPane.setToolTipText("Hier werden die Kunden angezeigt die gesucht werden");
				this.scrollPane.setBounds(0, 0, 284, 290);
				this.kundenlistePanel.add(this.scrollPane);
			}
		}
		{
			this.kundenlisteLabel = new JLabel("Kundenliste:");
			kundenlisteLabel.setBounds(10, 42, 185, 14);
			this.contentPane.add(this.kundenlisteLabel);
		}
		{
			this.kundenAnlegenButton = new JButton("Kunden anlegen");
			this.kundenAnlegenButton.setToolTipText("Hiermit gelangen Sie zur Kundenverwaltung, mit welcher Sie Kunden anlegen, \u00E4ndern oder entfernen k\u00F6nnen");
			kundenAnlegenButton.setBounds(10, 10, 135, 23);
			this.kundenAnlegenButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					do_kundeAnlegenButton_actionPerformed(arg0);
				}
			});
			this.contentPane.add(this.kundenAnlegenButton);
		}
		{
			this.leihdauerInTagenLabel = new JLabel("Leihdauer (in Tagen):");
			leihdauerInTagenLabel.setBounds(304, 61, 171, 14);
			this.contentPane.add(this.leihdauerInTagenLabel);
		}
		{
			this.leihdauerInTagenTextField = new JTextField();
			this.leihdauerInTagenTextField.setToolTipText("Geben Sie hier ein f\u00FCr wie lange das Spiel ausgeliehen werden soll");
			leihdauerInTagenTextField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if(leihdauerInTagenTextField.getText().length() <= 1 && ausleihmengeTextField.getText().length() <= 0) {
						preisBerechnenButton.setEnabled(false);
					} else if(leihdauerInTagenTextField.getText().length() >= 0 && ausleihmengeTextField.getText().length() >= 1) {
						preisBerechnenButton.setEnabled(true);
					}
				}
			});
			leihdauerInTagenTextField.setBounds(485, 58, 89, 20);
			this.contentPane.add(this.leihdauerInTagenTextField);
			this.leihdauerInTagenTextField.setColumns(10);
		}
		{
			this.ausleihpreisLabel = new JLabel("Ausleihpreis:");
			ausleihpreisLabel.setBounds(304, 148, 171, 14);
			this.contentPane.add(this.ausleihpreisLabel);
		}
		{
			this.ausleihpreisTextField = new JTextField();
			this.ausleihpreisTextField.setToolTipText("Hier wird angzeigt wie viel das Ausleihen insgesamt kostet");
			this.ausleihpreisTextField.setHorizontalAlignment(SwingConstants.RIGHT);
			this.ausleihpreisTextField.setEditable(false);
			ausleihpreisTextField.setBounds(485, 145, 50, 20);
			this.contentPane.add(this.ausleihpreisTextField);
			this.ausleihpreisTextField.setColumns(10);
		}
		{
			this.ausleihmengeLabel = new JLabel("Ausleihmenge:");
			ausleihmengeLabel.setBounds(304, 86, 171, 14);
			this.contentPane.add(this.ausleihmengeLabel);
		}
		{
			this.ausleihmengeTextField = new JTextField();
			this.ausleihmengeTextField.setToolTipText("Geben Sie hier die Menge an wie viele Exemplare ausgeliehen werden sollen");
			ausleihmengeTextField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if(leihdauerInTagenTextField.getText().length() <= 0 && ausleihmengeTextField.getText().length() <= 1) {
						preisBerechnenButton.setEnabled(false);
					} else if(leihdauerInTagenTextField.getText().length() >= 1 && ausleihmengeTextField.getText().length() >= 0) {
						preisBerechnenButton.setEnabled(true);
					}
				}
			});
			ausleihmengeTextField.setBounds(485, 83, 89, 20);
			this.contentPane.add(this.ausleihmengeTextField);
			this.ausleihmengeTextField.setColumns(10);
		}
		{
			this.preisBerechnenButton = new JButton("Preis berechnen");
			this.preisBerechnenButton.setToolTipText("Hiermit wird der Preis berechnet f\u00FCr das Ausleihen des Spiels");
			preisBerechnenButton.setBounds(370, 111, 135, 23);
			this.preisBerechnenButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						do_preisBerechnenButton_actionPerformed(e);
					} catch (ClassNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			this.contentPane.add(this.preisBerechnenButton);
		}
		{
			this.ausleihenButton = new JButton("Ausleihen");
			this.ausleihenButton.setToolTipText("Bei Klick wird der Ausleih-Prozess beendet");
			ausleihenButton.setEnabled(false);
			this.ausleihenButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					try {
						do_ausleihenButton_actionPerformed(arg0);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			this.ausleihenButton.setBounds(370, 173, 135, 23);
			this.contentPane.add(this.ausleihenButton);
		}
		{
			this.waehrungTextField = new JTextField();
			this.waehrungTextField.setText("\u20AC");
			this.waehrungTextField.setEditable(false);
			this.waehrungTextField.setBounds(545, 145, 29, 20);
			this.contentPane.add(this.waehrungTextField);
			this.waehrungTextField.setColumns(10);
		}
	}
	protected void do_suchenButton_actionPerformed(ActionEvent e) throws ClassNotFoundException, SQLException {
		try {
			fehlermeldungButton();
			ResultSet rs = kundenDAO.selectKundeAusleihfenster(kundensucheTextField.getText());
			this.kundenlisteTable.setModel(DbUtils.resultSetToTableModel(rs));
		} catch (Exception e2) {
			// TODO: handle exception
		}

	}
	protected void do_kundeAnlegenButton_actionPerformed(ActionEvent arg0) {
		Kundenverwaltung kundenverwaltung = new Kundenverwaltung();
		kundenverwaltung.setVisible(true);
	}
	protected void do_preisBerechnenButton_actionPerformed(ActionEvent e) throws ClassNotFoundException, SQLException {
		parsePrice();
		System.out.println("preisRechnen: " + this.ausleihpreisTextField.getText());
		ausleihenButton.setEnabled(true);
	}
	
	
	protected void do_kundenlisteTable_mouseClicked(MouseEvent e) {
		String ausgewaehlterKunde = kundeAuswaehlen.getKundennachnameInTable(kundenlisteTable);
	}
	public Spiel getSpieleDaten(String ausgewaehltesSpiel) throws ClassNotFoundException, SQLException {
		Spiel spiel = spielDAO.selectSpiel(ausgewaehltesSpiel);
		return spiel;
	}
	protected void do_ausleihenButton_actionPerformed(ActionEvent arg0) throws ClassNotFoundException, SQLException, ParseException {
		System.out.println("spielID: " + spiel);
		int lageranzahl = new SpielDAO().selectSpiel(spiel).getLageranzahl();
		String verfuegbarkeit = new SpielDAO().selectSpiel(spiel).getVerfuegbarkeit();
		if (lageranzahl != 0 && verfuegbarkeit.equalsIgnoreCase("verf�gbar")) {
			KundenSpiele ks = setKundenSpieleDaten(spiel);
			kundenSpieleDAO.insert(ks, spiel);
		}
		else if (lageranzahl == 0 && !verfuegbarkeit.equalsIgnoreCase("verf�gbar")) {
			JOptionPane alert = new JOptionPane();
			alert.showMessageDialog(this, "Das Spiel kann nicht mehr ausgeliehen werden!", "Fehler", JOptionPane.ERROR_MESSAGE);
		}
		else {
			System.out.println("Fehler beim Ausleihen!");
		}
	}
	
	public KundenSpiele setKundenSpieleDaten(String ausgewaehltesSpiel) throws ClassNotFoundException, SQLException, ParseException {
		System.out.println("test:" + ausgewaehltesSpiel); 
		Spiel spiel = getSpieleDaten(ausgewaehltesSpiel);
		KundenSpiele kundenSpiele = new KundenSpiele();
		kundenSpiele.setKundenID(kundeAuswaehlen.getWertInZeile(kundenlisteTable));
		kundenSpiele.setSpieleID(spiel.getId());
		kundenSpiele.setPreis(Double.valueOf(this.ausleihpreisTextField.getText().replace(',', '.')));
		kundenSpiele.setAusleihmenge(this.ausleihmengeTextField.getText());
		LocalDate currentDate = null;
		kundenSpiele.setAusleihdatum(String.valueOf(currentDate.now()));
		kundenSpiele.setFaelligkeitsdatum(String.valueOf(ermittelFaelligkeitsdatum()));
		return kundenSpiele;
	}
	
	public LocalDate ermittelFaelligkeitsdatum() throws ParseException {
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
		int leihdauer = Integer.valueOf(this.leihdauerInTagenTextField.getText());
		LocalDate date = LocalDate.now().plusDays(leihdauer);
	    System.out.println("faellig: " + date);
		return date;
	}
	
	
	public void fehlermeldungButton() throws ClassNotFoundException, SQLException {
		fenster = new Ausleihfenster(spiel);
		String gesuchtesSpiel = String.valueOf(kundensucheTextField.getText().trim());
		JOptionPane textFieldAlert = new JOptionPane();
		
		if(gesuchtesSpiel.equalsIgnoreCase("")) {
			textFieldAlert.showMessageDialog(this, "Bitte f�llen Sie das obere Feld aus!", "Fehler", JOptionPane.ERROR_MESSAGE);
			kundenlisteTable.setEnabled(false);
		} else {
			
		}
	}
	public void fehlermeldungPreisBerechnen() {
		if (leihdauerInTagenTextField.getText().equals("") && ausleihmengeTextField.getText().equals("")) {
			preisBerechnenButton.setEnabled(false);
		} 
	}
	public void fehlermeldungAusleihfenster() {
		if (ausleihmengeTextField.getText().equals("")) {
			ausleihenButton.setVisible(false);
		}
	}
	
	public void parsePrice() throws ClassNotFoundException, SQLException {
		JOptionPane alert = new JOptionPane();
		try {
			this.ausleihpreisProTag = Double.parseDouble(String.valueOf(getSpieleDaten(spiel).getPreis()));
			this.gesamtausleihpreis = Double.parseDouble(String.valueOf((Double.valueOf(this.leihdauerInTagenTextField.getText()) * ausleihpreisProTag)));
			if (this.gesamtausleihpreis > 0 && Double.valueOf(this.ausleihmengeTextField.getText()) > 0) {
				this.gesamtausleihpreis = Double.parseDouble(String.valueOf(this.gesamtausleihpreis * Double.valueOf(this.ausleihmengeTextField.getText())));
				this.ausleihpreisTextField.setText(String.valueOf(this.df.format(this.gesamtausleihpreis).replace('.', ',')));
			}
			else if (this.gesamtausleihpreis <= 0) {
				alert.showMessageDialog(this, "Ihre Eingabe '" + leihdauerInTagenTextField.getText() + "' ist nicht korrekt. Die Leihdauer muss mehr als einen Tag betragen.", "Fehler", JOptionPane.ERROR_MESSAGE);
			}
			else if (Double.valueOf(this.ausleihmengeTextField.getText()) <= 0) {
				alert.showMessageDialog(this, "Ihre Eingabe '" + ausleihmengeTextField.getText() + "' ist nicht korrekt. Die Ausleihmenge muss mindestens eins betragen.", "Fehler", JOptionPane.ERROR_MESSAGE);
			}
			else {
				System.out.println("Fehler");
			}
		} catch (NumberFormatException e) {
			alert.showMessageDialog(this, "Ihre Eingabe '" + leihdauerInTagenTextField.getText() + "' ist nicht korrekt. Es d�rfen nur positive Zahlen eingegeben werden.", "Fehler", JOptionPane.ERROR_MESSAGE);
		}
	}
}