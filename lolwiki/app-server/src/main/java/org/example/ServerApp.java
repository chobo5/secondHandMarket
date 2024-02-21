package org.example;

import org.example.dao.ClubGeneralDaoImpl;
import org.example.dao.GeneralDao;
import org.example.dao.LeagueGeneralDaoImpl;
import org.example.dao.PlayerGeneralDaoImpl;
import org.example.menu.Menu;
import org.example.menu.MenuGroup;
import org.example.menu.MenuItem;
import org.example.menu.handler.club.ClubListHandler;
import org.example.menu.handler.league.LeagueAddHandler;
import org.example.menu.handler.league.LeagueListHandler;
import org.example.menu.handler.player.PlayerListHandler;
import org.example.util.DBConnectionPool;
import org.example.util.Prompt;
import org.example.vo.Club;
import org.example.vo.League;
import org.example.vo.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerApp {
    DBConnectionPool connectionPool;
    MenuGroup mainMenu;
    GeneralDao<League> leagueDao;
    GeneralDao<Club> clubDao;
    GeneralDao<Player> playerDao;

    ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        new ServerApp().run();
    }

    public ServerApp() {
        prepareDatabase();
        prepareMenus();
    }

    public void prepareDatabase() {
        try {
            connectionPool = new DBConnectionPool(
                    "jdbc:mysql://db-ld296-kr.vpc-pub-cdb.ntruss.com/private",
                    "study", "Bitcamp!@#123");
            leagueDao = new LeagueGeneralDaoImpl(connectionPool);
            clubDao = new ClubGeneralDaoImpl(connectionPool);
            playerDao = new PlayerGeneralDaoImpl(connectionPool);
        } catch (Exception e) {
            System.out.println("데이터 베이스 연결 실패");
            e.printStackTrace();
        }
    }

    public void prepareMenus() {
        mainMenu = new MenuGroup("메인");


        Menu leagueMenu = new Menu("리그");
        leagueMenu.addMenuItem(new MenuItem(new LeagueListHandler(leagueDao), "리그 목록"));
        leagueMenu.addMenuItem(new MenuItem(new LeagueAddHandler(leagueDao), "리그 추가"));
        mainMenu.addMenu(leagueMenu);


        Menu clubMenu = new Menu("구단");
        clubMenu.addMenuItem(new MenuItem(new ClubListHandler(clubDao),"구단 목록"));
        mainMenu.addMenu(clubMenu);


        Menu playerMenu = new Menu("선수");
        playerMenu.addMenuItem(new MenuItem(new PlayerListHandler(playerDao), "선수 목록"));
        mainMenu.addMenu(playerMenu);


    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(8888)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("클라이언트 연결됨");
                executorService.execute(() -> processRequest(socket));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void processRequest(Socket socket) {
        try (DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             Prompt prompt = new Prompt(in, out);) {
            try {
                mainMenu.execute(prompt);
            } catch (Exception e) {
                prompt.println("프로그램을 종료합니다.");
            }

        } catch (Exception e) {

        }
    }
}