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

		// 트리랑 리스너 연결
		tree.addTreeSelectionListener(this);

		setSize(700, 800);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public void createNode(DefaultMutableTreeNode root) {
		root = new DefaultMutableTreeNode("과일");
		DefaultMutableTreeNode node1 = null;
		DefaultMutableTreeNode node2 = null;
		DefaultMutableTreeNode node3 = null;

		node1 = new DefaultMutableTreeNode("블루베리");
		node2 = new DefaultMutableTreeNode("레몬");
		node3 = new DefaultMutableTreeNode("수입산과일");

		root.add(node1);
		root.add(node2);
		root.add(node3);

		DefaultMutableTreeNode node4 = new DefaultMutableTreeNode("치즈맛딸기");
		DefaultMutableTreeNode node5 = new DefaultMutableTreeNode("아보카도");

		node3.add(node4);
		node3.add(node5);
	}

	// 윈도우의 구조를 보여주기 (파일 탐색기)
	public void createDirectory() {
		root = new DefaultMutableTreeNode("내컴퓨터");
		File[] drive = File.listRoots();
		FileSystemView fsv = FileSystemView.getFileSystemView();

		for (int i = 0; i < drive.length; i++) {
			String volumn = fsv.getSystemDisplayName(drive[i]);
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(volumn);
			root.add(node);
		}
	}

	//디렉토리 만들기
	public void createMusicDir() {
		root = new DefaultMutableTreeNode("쥬크박스");
		File file = new File(path + "data");
		File[] child = file.listFiles();

		for (int i = 0; i < child.length; i++) {
			DefaultMutableTreeNode node = null;
			node = new DefaultMutableTreeNode(child[i].getName());
			root.add(node);
		}
	}

	// 선택한 노드의 파일에 대한 정보 추출하기
	public void extract(String filename) {
		fileLocation = path + "data/" + filename;

		// 음악 틀기
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

	// 선택한 MP3 파일 재생, JLayer
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
