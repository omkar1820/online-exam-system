package bankaccountmanager;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.List;

public class MainFrame extends JFrame {

    private AccountManager manager = new AccountManager();
    private JTable accountTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JLabel totalLabel;
    private JLabel totalFundsLabel;

    // ── Yashuux Brand Palette ──────────────────────────────────────────────────
    private static final Color YT_RED      = new Color(255, 0, 0);
    private static final Color YT_RED_DARK = new Color(200, 0, 0);
    private static final Color DARK_BG     = new Color(10, 10, 14);
    private static final Color SURFACE     = new Color(18, 18, 24);
    private static final Color SURFACE_2   = new Color(26, 26, 36);
    private static final Color BORDER_COL  = new Color(40, 40, 58);
    private static final Color GOLD        = new Color(255, 200, 50);
    private static final Color SUCCESS     = new Color(0, 230, 118);
    private static final Color DANGER      = new Color(255, 82, 82);
    private static final Color PURPLE      = new Color(156, 39, 176);
    private static final Color TEAL        = new Color(0, 200, 180);
    private static final Color TEXT_MAIN   = new Color(245, 245, 245);
    private static final Color TEXT_MUTED  = new Color(120, 120, 140);
    private static final Color TEXT_DIM    = new Color(80, 80, 100);
    private static final Color CYAN        = new Color(0, 229, 255);

    public MainFrame() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        initComponents();
        refreshTable();
    }

    private void initComponents() {
        setTitle("Yashuux Bank — Account Manager");
        setSize(1150, 720);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(DARK_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                RadialGradientPaint glow = new RadialGradientPaint(
                    new Point2D.Float(0, 0), 400f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(180, 0, 0, 35), new Color(0, 0, 0, 0)}
                );
                g2.setPaint(glow);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        root.setOpaque(true);
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMainContent(), BorderLayout.CENTER);
        root.add(buildStatusBar(), BorderLayout.SOUTH);
    }

    // ── YouTube-Style Logo ────────────────────────────────────────────────────
    private JPanel buildYoutubeLogoPanel() {
        return new JPanel() {
            { setOpaque(false); setPreferredSize(new Dimension(155, 40)); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                // Red rounded rect
                int bx = 0, by = 5, bw = 44, bh = 30;
                g2.setColor(YT_RED);
                g2.fillRoundRect(bx, by, bw, bh, 10, 10);
                // Shadow under play icon
                g2.setColor(new Color(0,0,0,60));
                g2.fillOval(bx+10, by+18, 24, 8);
                // White play triangle
                int cx = bx + bw/2 + 3, cy = by + bh/2;
                int[] px = {cx-8, cx-8, cx+10};
                int[] py = {cy-8, cy+8, cy};
                g2.setColor(Color.WHITE);
                g2.fillPolygon(px, py, 3);
                // Bank name
                g2.setFont(new Font("Georgia", Font.BOLD, 18));
                g2.setColor(Color.WHITE);
                g2.drawString("Yashuux", bw+8, by+15);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2.setColor(new Color(200,200,200));
                g2.drawString("B A N K", bw+10, by+29);
                g2.dispose();
            }
        };
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(SURFACE);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(YT_RED);
                g.fillRect(0, getHeight()-2, getWidth(), 2);
            }
        };
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(buildYoutubeLogoPanel());
        JLabel tagline = new JLabel("   Premium Banking Experience");
        tagline.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        tagline.setForeground(TEXT_DIM);
        left.add(tagline);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        right.setOpaque(false);

        JPanel acctChip = buildStatChip("ACCOUNTS", "0", CYAN);
        JPanel fundChip = buildStatChip("TOTAL FUNDS", "\u20b90.00", GOLD);
        totalLabel      = (JLabel)((JPanel)acctChip.getComponent(1)).getComponent(0);
        totalFundsLabel = (JLabel)((JPanel)fundChip.getComponent(1)).getComponent(0);

        right.add(acctChip);
        right.add(fundChip);

        JLabel clock = new JLabel();
        clock.setFont(new Font("Courier New", Font.BOLD, 14));
        clock.setForeground(TEXT_MUTED);
        right.add(clock);
        new Timer(1000, e -> {
            java.time.LocalTime now = java.time.LocalTime.now();
            clock.setText(String.format("%02d:%02d:%02d", now.getHour(), now.getMinute(), now.getSecond()));
        }).start();

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JPanel buildStatChip(String lbl, String val, Color accent) {
        JPanel chip = new JPanel(new BorderLayout(2,2)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SURFACE_2); g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.setColor(accent); g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.dispose();
            }
        };
        chip.setOpaque(false);
        chip.setBorder(BorderFactory.createEmptyBorder(5,12,5,12));
        JLabel label = new JLabel(lbl); label.setFont(new Font("Segoe UI",Font.BOLD,8)); label.setForeground(accent);
        JPanel valP = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0)); valP.setOpaque(false);
        JLabel valL = new JLabel(val); valL.setFont(new Font("Segoe UI",Font.BOLD,14)); valL.setForeground(TEXT_MAIN);
        valP.add(valL);
        chip.add(label, BorderLayout.NORTH);
        chip.add(valP,  BorderLayout.CENTER);
        return chip;
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel side = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(SURFACE); g.fillRect(0,0,getWidth(),getHeight());
                g.setColor(BORDER_COL); g.fillRect(getWidth()-1,0,1,getHeight());
            }
        };
        side.setOpaque(false);
        side.setPreferredSize(new Dimension(210,0));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(BorderFactory.createEmptyBorder(18,10,18,10));

        side.add(buildAvatarSection());
        side.add(Box.createVerticalStrut(16));
        side.add(makeDivider());
        side.add(Box.createVerticalStrut(14));

        side.add(sectionLabel("ACCOUNT OPS"));
        side.add(Box.createVerticalStrut(6));
        side.add(sideBtn("  +  Create Account",   YT_RED,   e -> showCreateDialog()));
        side.add(Box.createVerticalStrut(5));
        side.add(sideBtn("  /  Edit Account",      GOLD,     e -> editAccount()));
        side.add(Box.createVerticalStrut(5));
        side.add(sideBtn("  x  Delete Account",    DANGER,   e -> deleteAccount()));
        side.add(Box.createVerticalStrut(16));

        side.add(sectionLabel("TRANSACTIONS"));
        side.add(Box.createVerticalStrut(6));
        side.add(sideBtn("  ^ Deposit",             SUCCESS,  e -> showDepositDialog()));
        side.add(Box.createVerticalStrut(5));
        side.add(sideBtn("  v Withdraw",             DANGER,   e -> showWithdrawDialog()));
        side.add(Box.createVerticalStrut(5));
        side.add(sideBtn("  ~ Transfer",             PURPLE,   e -> showTransferDialog()));
        side.add(Box.createVerticalStrut(16));

        side.add(sectionLabel("TOOLS"));
        side.add(Box.createVerticalStrut(6));
        side.add(sideBtn("  = Statement",            TEAL,     e -> viewStatement()));
        side.add(Box.createVerticalStrut(5));
        side.add(sideBtn("  ? Search by Name",       CYAN,     e -> searchAccount()));
        side.add(Box.createVerticalStrut(5));
        side.add(sideBtn("  @ Refresh",              TEXT_MUTED, e -> { refreshTable(); setStatus("Table refreshed."); }));

        side.add(Box.createVerticalGlue());
        side.add(makeDivider());
        side.add(Box.createVerticalStrut(8));
        JLabel ver = new JLabel("  v2.0  Yashuux Bank");
        ver.setFont(new Font("Segoe UI",Font.PLAIN,10)); ver.setForeground(TEXT_DIM);
        side.add(ver);
        return side;
    }

    private JPanel buildAvatarSection() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));
        p.setOpaque(false); p.setMaximumSize(new Dimension(Integer.MAX_VALUE,50));
        JPanel avatar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0,0,YT_RED,36,36,YT_RED_DARK);
                g2.setPaint(gp); g2.fillOval(0,0,36,36);
                g2.setFont(new Font("Georgia",Font.BOLD,16)); g2.setColor(Color.WHITE);
                g2.drawString("Y", 11, 25);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(36,36); }
        };
        avatar.setOpaque(false);
        JPanel info = new JPanel(); info.setOpaque(false);
        info.setLayout(new BoxLayout(info,BoxLayout.Y_AXIS));
        JLabel name = new JLabel("Yashuux Admin");
        name.setFont(new Font("Segoe UI",Font.BOLD,12)); name.setForeground(TEXT_MAIN);
        JLabel role = new JLabel("Branch Manager");
        role.setFont(new Font("Segoe UI",Font.PLAIN,10)); role.setForeground(YT_RED);
        info.add(name); info.add(role);
        p.add(avatar); p.add(info);
        return p;
    }

    private JSeparator makeDivider() {
        JSeparator s = new JSeparator(); s.setForeground(BORDER_COL);
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE,1)); return s;
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI",Font.BOLD,9)); l.setForeground(TEXT_DIM);
        l.setAlignmentX(Component.LEFT_ALIGNMENT); return l;
    }

    private JButton sideBtn(String text, Color accent, ActionListener act) {
        JButton btn = new JButton(text) {
            private boolean hov = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e){hov=true;repaint();}
                public void mouseExited(MouseEvent e){hov=false;repaint();}
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                if(hov){
                    g2.setColor(new Color(accent.getRed(),accent.getGreen(),accent.getBlue(),28));
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                    g2.setColor(accent); g2.fillRoundRect(0,6,3,getHeight()-12,3,3);
                }
                g2.setFont(getFont());
                g2.setColor(hov ? accent : new Color(180,180,195));
                g2.drawString(getText(),10,getHeight()/2+g2.getFontMetrics().getAscent()/2-1);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI",Font.PLAIN,13));
        btn.setOpaque(false); btn.setContentAreaFilled(false); btn.setBorderPainted(false);
        btn.setFocusPainted(false); btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE,34));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.addActionListener(act); return btn;
    }

    // ── Main Content ──────────────────────────────────────────────────────────
    private JPanel buildMainContent() {
        JPanel main = new JPanel(new BorderLayout(0,0)); main.setOpaque(false);
        JPanel topBar = new JPanel(new BorderLayout()); topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(16,18,10,18));
        JLabel title = new JLabel("All Accounts");
        title.setFont(new Font("Georgia",Font.BOLD,20)); title.setForeground(TEXT_MAIN);
        JLabel sub = new JLabel("   Manage and monitor all customer accounts");
        sub.setFont(new Font("Segoe UI",Font.ITALIC,12)); sub.setForeground(TEXT_MUTED);
        JPanel tg = new JPanel(new FlowLayout(FlowLayout.LEFT,0,2)); tg.setOpaque(false);
        tg.add(title); tg.add(sub); topBar.add(tg,BorderLayout.WEST);
        JPanel qb = new JPanel(new FlowLayout(FlowLayout.RIGHT,6,0)); qb.setOpaque(false);
        qb.add(quickBtn("+ New",YT_RED,e->showCreateDialog()));
        qb.add(quickBtn("Deposit",SUCCESS,e->showDepositDialog()));
        qb.add(quickBtn("Withdraw",DANGER,e->showWithdrawDialog()));
        topBar.add(qb,BorderLayout.EAST);
        main.add(topBar,BorderLayout.NORTH);
        main.add(buildTablePanel(),BorderLayout.CENTER);
        return main;
    }

    private JButton quickBtn(String text, Color color, ActionListener act) {
        JButton btn = new JButton(text) {
            private boolean hov=false;
            { addMouseListener(new MouseAdapter(){
                public void mouseEntered(MouseEvent e){hov=true;repaint();}
                public void mouseExited(MouseEvent e){hov=false;repaint();}
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg=hov?color:new Color(color.getRed(),color.getGreen(),color.getBlue(),180);
                g2.setColor(bg); g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g2.setFont(getFont()); g2.setColor(Color.WHITE);
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI",Font.BOLD,12)); btn.setOpaque(false);
        btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(90,30)); btn.addActionListener(act); return btn;
    }

    // ── Table ─────────────────────────────────────────────────────────────────
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SURFACE); g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,14,14);
                g2.setColor(BORDER_COL); g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,14,14);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0,18,16,18));

        String[] cols={"  Account No","  Holder Name","  Type","  Balance (Rs.)"};
        tableModel=new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};

        accountTable=new JTable(tableModel){
            @Override public Component prepareRenderer(TableCellRenderer r,int row,int col){
                Component c=super.prepareRenderer(r,row,col);
                if(isRowSelected(row)){c.setBackground(new Color(255,0,0,40));c.setForeground(TEXT_MAIN);}
                else{c.setBackground(row%2==0?SURFACE:SURFACE_2);c.setForeground(col==3?GOLD:TEXT_MAIN);}
                if(c instanceof JLabel)((JLabel)c).setBorder(BorderFactory.createEmptyBorder(0,8,0,8));
                return c;
            }
        };
        accountTable.setBackground(SURFACE); accountTable.setForeground(TEXT_MAIN);
        accountTable.setFont(new Font("Segoe UI",Font.PLAIN,13)); accountTable.setRowHeight(38);
        accountTable.setGridColor(BORDER_COL); accountTable.setShowHorizontalLines(true);
        accountTable.setShowVerticalLines(false); accountTable.setIntercellSpacing(new Dimension(0,1));
        accountTable.setSelectionBackground(new Color(255,0,0,50)); accountTable.setSelectionForeground(TEXT_MAIN);

        JTableHeader th=accountTable.getTableHeader();
        th.setBackground(new Color(20,20,28)); th.setForeground(YT_RED);
        th.setFont(new Font("Segoe UI",Font.BOLD,12));
        th.setBorder(BorderFactory.createMatteBorder(0,0,2,0,YT_RED));
        th.setReorderingAllowed(false);

        DefaultTableCellRenderer rr=new DefaultTableCellRenderer(){
            {setHorizontalAlignment(SwingConstants.RIGHT);}
            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int c){
                super.getTableCellRendererComponent(t,v,s,f,r,c);
                setForeground(s?Color.WHITE:GOLD);
                setFont(new Font("Courier New",Font.BOLD,13)); return this;
            }
        };
        accountTable.getColumnModel().getColumn(3).setCellRenderer(rr);
        accountTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        accountTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        accountTable.getColumnModel().getColumn(2).setPreferredWidth(130);
        accountTable.getColumnModel().getColumn(3).setPreferredWidth(140);

        JScrollPane scroll=new JScrollPane(accountTable);
        scroll.getViewport().setBackground(SURFACE);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COL));
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        panel.add(scroll,BorderLayout.CENTER); return panel;
    }

    // ── Status Bar ────────────────────────────────────────────────────────────
    private JPanel buildStatusBar() {
        JPanel bar=new JPanel(new BorderLayout()){
            @Override protected void paintComponent(Graphics g){g.setColor(new Color(8,8,12));g.fillRect(0,0,getWidth(),getHeight());}
        };
        bar.setOpaque(false); bar.setBorder(BorderFactory.createEmptyBorder(5,16,5,16));
        statusLabel=new JLabel("System ready -- Welcome to Yashuux Bank");
        statusLabel.setForeground(TEXT_MUTED); statusLabel.setFont(new Font("Segoe UI",Font.PLAIN,11));
        JLabel copy=new JLabel("(c) 2025 Yashuux Bank. All rights reserved.");
        copy.setFont(new Font("Segoe UI",Font.PLAIN,10)); copy.setForeground(TEXT_DIM);
        bar.add(statusLabel,BorderLayout.WEST); bar.add(copy,BorderLayout.EAST); return bar;
    }

    // ── Refresh ───────────────────────────────────────────────────────────────
    private void refreshTable() {
        tableModel.setRowCount(0);
        for(Account a:manager.getAllAccounts()){
            tableModel.addRow(new Object[]{
                a.getAccountNumber(),a.getHolderName(),a.getAccountType(),
                String.format("Rs. %.2f",a.getBalance())
            });
        }
        int count=manager.getAllAccounts().size();
        double funds=manager.getTotalDeposits();
        if(totalLabel!=null) totalLabel.setText(String.valueOf(count));
        if(totalFundsLabel!=null) totalFundsLabel.setText(String.format("Rs.%.0f",funds));
    }

    private void setStatus(String msg){statusLabel.setText(msg);}

    private void styleOptionPane(){
        UIManager.put("OptionPane.background",SURFACE);
        UIManager.put("Panel.background",SURFACE);
        UIManager.put("OptionPane.messageForeground",TEXT_MAIN);
        UIManager.put("Button.background",YT_RED);
        UIManager.put("Button.foreground",Color.WHITE);
        UIManager.put("Button.font",new Font("Segoe UI",Font.BOLD,12));
    }

    private JPanel dlgPanel(){
        JPanel p=new JPanel(new GridLayout(0,2,10,10)){
            @Override protected void paintComponent(Graphics g){g.setColor(SURFACE);g.fillRect(0,0,getWidth(),getHeight());}
        };
        p.setOpaque(true); p.setBorder(BorderFactory.createEmptyBorder(16,16,16,16)); return p;
    }

    private JLabel dlgLbl(String t){
        JLabel l=new JLabel(t); l.setFont(new Font("Segoe UI",Font.PLAIN,13)); l.setForeground(TEXT_MUTED); return l;
    }

    private JTextField dlgField(String ph,int cols){
        JTextField f=new JTextField(ph,cols);
        f.setBackground(SURFACE_2); f.setForeground(TEXT_MAIN); f.setCaretColor(YT_RED);
        f.setFont(new Font("Segoe UI",Font.PLAIN,13));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COL),
            BorderFactory.createEmptyBorder(4,8,4,8))); return f;
    }

    private JComboBox<String> dlgCombo(String[] items){
        JComboBox<String> c=new JComboBox<>(items);
        c.setBackground(SURFACE_2); c.setForeground(TEXT_MAIN);
        c.setFont(new Font("Segoe UI",Font.PLAIN,13)); return c;
    }

    // ── Dialogs ───────────────────────────────────────────────────────────────
    private void showCreateDialog(){
        styleOptionPane();
        JTextField nameF=dlgField("e.g. Rahul Sharma",18);
        JComboBox<String> typeB=dlgCombo(new String[]{"Savings","Current","Fixed Deposit"});
        JTextField balF=dlgField("500.00",18);
        JPanel p=dlgPanel();
        p.add(dlgLbl("Holder Name:")); p.add(nameF);
        p.add(dlgLbl("Account Type:")); p.add(typeB);
        p.add(dlgLbl("Initial Deposit (Rs.):")); p.add(balF);
        int r=JOptionPane.showConfirmDialog(this,p,"Create New Account -- Yashuux Bank",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
        if(r==JOptionPane.OK_OPTION){
            String name=nameF.getText().trim(); if(name.isEmpty()){showError("Name cannot be empty.");return;}
            try{double bal=Double.parseDouble(balF.getText().trim());if(bal<0)throw new NumberFormatException();
                Account acc=manager.createAccount(name,(String)typeB.getSelectedItem(),bal);
                refreshTable(); setStatus("Account "+acc.getAccountNumber()+" created for "+name);
            }catch(NumberFormatException ex){showError("Invalid initial deposit amount.");}
        }
    }

    private void deleteAccount(){
        String accNo=getSelectedAccountNumber(); if(accNo==null)return; styleOptionPane();
        int c=JOptionPane.showConfirmDialog(this,"Delete account "+accNo+"? This cannot be undone.","Confirm Delete",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
        if(c==JOptionPane.YES_OPTION){manager.deleteAccount(accNo);refreshTable();setStatus("Account "+accNo+" deleted.");}
    }

    private void editAccount(){
        String accNo=getSelectedAccountNumber(); if(accNo==null)return;
        Account acc=manager.findAccount(accNo); styleOptionPane();
        JTextField nameF=dlgField(acc.getHolderName(),18);
        JComboBox<String> typeB=dlgCombo(new String[]{"Savings","Current","Fixed Deposit"});
        typeB.setSelectedItem(acc.getAccountType());
        JPanel p=dlgPanel(); p.add(dlgLbl("Holder Name:")); p.add(nameF); p.add(dlgLbl("Account Type:")); p.add(typeB);
        int r=JOptionPane.showConfirmDialog(this,p,"Edit Account "+accNo+" -- Yashuux Bank",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
        if(r==JOptionPane.OK_OPTION){String name=nameF.getText().trim();if(name.isEmpty()){showError("Name cannot be empty.");return;}
            acc.setHolderName(name);acc.setAccountType((String)typeB.getSelectedItem());refreshTable();setStatus("Account "+accNo+" updated.");}
    }

    private void showDepositDialog(){
        String accNo=getSelectedAccountNumber(); if(accNo==null)return;
        Account acc=manager.findAccount(accNo); styleOptionPane();
        JTextField amtF=dlgField("",12); JPanel p=dlgPanel();
        p.add(dlgLbl("Account:")); p.add(dlgLbl(accNo+" ("+acc.getHolderName()+")"));
        p.add(dlgLbl("Balance:")); p.add(dlgLbl("Rs. "+String.format("%.2f",acc.getBalance())));
        p.add(dlgLbl("Deposit (Rs.):")); p.add(amtF);
        int r=JOptionPane.showConfirmDialog(this,p,"Deposit -- Yashuux Bank",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
        if(r==JOptionPane.OK_OPTION){try{double amt=Double.parseDouble(amtF.getText().trim());if(amt<=0)throw new NumberFormatException();
            acc.deposit(amt);refreshTable();setStatus("Deposited Rs."+String.format("%.2f",amt)+" to "+accNo);
        }catch(NumberFormatException ex){showError("Invalid amount.");}}
    }

    private void showWithdrawDialog(){
        String accNo=getSelectedAccountNumber(); if(accNo==null)return;
        Account acc=manager.findAccount(accNo); styleOptionPane();
        JTextField amtF=dlgField("",12); JPanel p=dlgPanel();
        p.add(dlgLbl("Account:")); p.add(dlgLbl(accNo+" ("+acc.getHolderName()+")"));
        p.add(dlgLbl("Available:")); p.add(dlgLbl("Rs. "+String.format("%.2f",acc.getBalance())));
        p.add(dlgLbl("Withdraw (Rs.):")); p.add(amtF);
        int r=JOptionPane.showConfirmDialog(this,p,"Withdraw -- Yashuux Bank",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
        if(r==JOptionPane.OK_OPTION){try{double amt=Double.parseDouble(amtF.getText().trim());if(amt<=0)throw new NumberFormatException();
            if(!acc.withdraw(amt)){showError("Insufficient balance!");return;}
            refreshTable();setStatus("Withdrew Rs."+String.format("%.2f",amt)+" from "+accNo);
        }catch(NumberFormatException ex){showError("Invalid amount.");}}
    }

    private void showTransferDialog(){
        String accNo=getSelectedAccountNumber(); if(accNo==null)return;
        Account src=manager.findAccount(accNo); styleOptionPane();
        JTextField tgtF=dlgField("",12),amtF=dlgField("",12); JPanel p=dlgPanel();
        p.add(dlgLbl("From:")); p.add(dlgLbl(accNo+" ("+src.getHolderName()+")"));
        p.add(dlgLbl("Available:")); p.add(dlgLbl("Rs. "+String.format("%.2f",src.getBalance())));
        p.add(dlgLbl("To Account No:")); p.add(tgtF);
        p.add(dlgLbl("Amount (Rs.):")); p.add(amtF);
        int r=JOptionPane.showConfirmDialog(this,p,"Transfer -- Yashuux Bank",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
        if(r==JOptionPane.OK_OPTION){Account dest=manager.findAccount(tgtF.getText().trim());
            if(dest==null){showError("Destination account not found.");return;}
            if(dest.getAccountNumber().equals(accNo)){showError("Cannot transfer to same account.");return;}
            try{double amt=Double.parseDouble(amtF.getText().trim());if(amt<=0)throw new NumberFormatException();
                if(!src.transfer(dest,amt)){showError("Insufficient balance!");return;}
                refreshTable();setStatus("Transferred Rs."+String.format("%.2f",amt)+" to "+dest.getAccountNumber());
            }catch(NumberFormatException ex){showError("Invalid amount.");}}
    }

    private void viewStatement(){
        String accNo=getSelectedAccountNumber(); if(accNo==null)return;
        Account acc=manager.findAccount(accNo); styleOptionPane();
        StringBuilder sb=new StringBuilder();
        sb.append("==============================================\n");
        sb.append("       YASHUUX BANK -- ACCOUNT STATEMENT     \n");
        sb.append("==============================================\n");
        sb.append(String.format("  Account No  : %s%n",acc.getAccountNumber()));
        sb.append(String.format("  Holder      : %s%n",acc.getHolderName()));
        sb.append(String.format("  Type        : %s%n",acc.getAccountType()));
        sb.append(String.format("  Balance     : Rs. %.2f%n",acc.getBalance()));
        sb.append("----------------------------------------------\n  TRANSACTION HISTORY\n\n");
        for(String t:acc.getTransactions()) sb.append("  >  ").append(t).append("\n");
        sb.append("==============================================\n");
        JTextArea ta=new JTextArea(sb.toString(),18,52);
        ta.setEditable(false); ta.setFont(new Font("Courier New",Font.PLAIN,12));
        ta.setBackground(SURFACE); ta.setForeground(GOLD);
        JScrollPane sp=new JScrollPane(ta);
        sp.setBorder(BorderFactory.createLineBorder(YT_RED));
        sp.getViewport().setBackground(SURFACE);
        JOptionPane.showMessageDialog(this,sp,"Statement -- "+accNo,JOptionPane.PLAIN_MESSAGE);
    }

    private void searchAccount(){
        styleOptionPane();
        JTextField sf=dlgField("Enter name...",20); JPanel p=dlgPanel();
        p.add(dlgLbl("Search Name:")); p.add(sf);
        int r=JOptionPane.showConfirmDialog(this,p,"Search -- Yashuux Bank",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
        if(r!=JOptionPane.OK_OPTION)return; String name=sf.getText().trim(); if(name.isBlank())return;
        List<Account> found=manager.searchByName(name);
        if(found.isEmpty()){setStatus("No accounts found for: "+name);JOptionPane.showMessageDialog(this,"No accounts found matching: "+name);return;}
        tableModel.setRowCount(0);
        for(Account a:found) tableModel.addRow(new Object[]{a.getAccountNumber(),a.getHolderName(),a.getAccountType(),String.format("Rs. %.2f",a.getBalance())});
        setStatus("Found "+found.size()+" account(s) for: "+name+"  (Refresh to show all)");
    }

    private String getSelectedAccountNumber(){
        int row=accountTable.getSelectedRow();
        if(row<0){JOptionPane.showMessageDialog(this,"Please select an account from the table first.","No Selection",JOptionPane.INFORMATION_MESSAGE);return null;}
        return ((String)tableModel.getValueAt(row,0)).trim();
    }

    private void showError(String msg){styleOptionPane();JOptionPane.showMessageDialog(this,msg,"Yashuux Bank -- Error",JOptionPane.ERROR_MESSAGE);}
}
