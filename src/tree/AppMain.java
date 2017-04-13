package tree;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.LyricsHandler;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class AppMain extends JFrame implements TreeSelectionListener, Runnable {
	JTree tree;
	JScrollPane scroll;
	JPanel p_west, p_center;
	DefaultMutableTreeNode root;
	JTextArea area;
	String path = "C:/java_workspace2/TreeProject/";
	String fileLocation;
	Player player;

	public AppMain() {
		p_west = new JPanel();
		// p_center = new JPanel();

		// createNode(root);
		// createDirectory();
		createMusicDir();

		tree = new JTree(root);
		scroll = new JScrollPane(tree);

		area = new JTextArea();

		p_west.setLayout(new BorderLayout());
		p_west.setPreferredSize(new Dimension(200, 500));

		p_west.add(scroll);
		add(p_west, BorderLayout.WEST);
		add(area);

		// Ʈ���� ������ ����
		tree.addTreeSelectionListener(this);

		setSize(700, 800);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public void createNode(DefaultMutableTreeNode root) {
		root = new DefaultMutableTreeNode("����");
		DefaultMutableTreeNode node1 = null;
		DefaultMutableTreeNode node2 = null;
		DefaultMutableTreeNode node3 = null;

		node1 = new DefaultMutableTreeNode("��纣��");
		node2 = new DefaultMutableTreeNode("����");
		node3 = new DefaultMutableTreeNode("���Ի����");

		root.add(node1);
		root.add(node2);
		root.add(node3);

		DefaultMutableTreeNode node4 = new DefaultMutableTreeNode("ġ�������");
		DefaultMutableTreeNode node5 = new DefaultMutableTreeNode("�ƺ�ī��");

		node3.add(node4);
		node3.add(node5);
	}

	// �������� ������ �����ֱ� (���� Ž����)
	public void createDirectory() {
		root = new DefaultMutableTreeNode("����ǻ��");
		File[] drive = File.listRoots();
		FileSystemView fsv = FileSystemView.getFileSystemView();

		for (int i = 0; i < drive.length; i++) {
			String volumn = fsv.getSystemDisplayName(drive[i]);
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(volumn);
			root.add(node);
		}
	}

	//���丮 �����
	public void createMusicDir() {
		root = new DefaultMutableTreeNode("��ũ�ڽ�");
		File file = new File(path + "data");
		File[] child = file.listFiles();

		for (int i = 0; i < child.length; i++) {
			DefaultMutableTreeNode node = null;
			node = new DefaultMutableTreeNode(child[i].getName());
			root.add(node);
		}
	}

	// ������ ����� ���Ͽ� ���� ���� �����ϱ�
	public void extract(String filename) {
		fileLocation = path + "data/" + filename;

		// ���� Ʋ��
		Thread thread = new Thread(this);
		thread.start();

		BodyContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		FileInputStream inputstream = null;
		try {
			inputstream = new FileInputStream(new File(fileLocation));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		ParseContext pcontext = new ParseContext();

		// Mp3 parser
		Mp3Parser Mp3Parser = new Mp3Parser();

		LyricsHandler lyrics;
		try {
			Mp3Parser.parse(inputstream, handler, metadata, pcontext);
			lyrics = new LyricsHandler(inputstream, handler);
			while (lyrics.hasLyrics()) {
				System.out.println(lyrics.toString());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TikaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		area.append("Contents of the document:" + handler.toString());
		area.append("Metadata of the document:");
		String[] metadataNames = metadata.names();

		for (String name : metadataNames) {
			area.append(name + ": " + metadata.get(name) + "\n");
		}

	}

	// ������ MP3 ���� ���, JLayer
	public void play() {
		if (player != null) {
			player.close();
		}

		FileInputStream stream = null;
		try {
			File file = new File(fileLocation);
			stream = new FileInputStream(file);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			player = new Player(stream);
			player.play();
		} catch (JavaLayerException e) {
			e.printStackTrace();
		}

	}

	public void run() {
		play();
	}

	public void valueChanged(TreeSelectionEvent e) {
		Object obj = e.getSource();
		JTree tree = (JTree) obj;
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		area.setText("");
		extract(node.getUserObject().toString());

	}

	public static void main(String[] args) {
		new AppMain();

	}

}
