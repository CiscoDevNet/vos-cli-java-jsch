import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jcraft.jsch.*;

public class vos_cli_java_jsch {

    private static JSch jsch;
    private static Session session;
    private static Channel channel;
    private static InputStream in;
    private static OutputStream out;

    static class ExpectChannelClosedException extends Exception {
        public ExpectChannelClosedException(String errorMessage) {
            super(errorMessage);
        }
    }

    static class ExpectTimedOutException extends Exception {
        public ExpectTimedOutException() {
            super();
        }
    }

    public static void main(String[] args) throws Exception {
        try {
            String vos_user_name = System.getenv("VOS_USER_NAME");
            String vos_user_password = System.getenv("VOS_USER_PASSWORD");
            String vos_host_name = System.getenv("VOS_HOST_NAME");

            if (vos_user_name == null || vos_user_password == null || vos_host_name == null) {
                System.out.println("\n-> ERROR: Required environment variables not set\n");
                System.exit(1);
            }
            jsch = new JSch();
            session = jsch.getSession("Administrator", "sjds-cucm14.cisco.com", 22);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            // To enable SSH host key checking, disable the line above, and enable the line
            // below (update path)
            // jsch.setKnownHosts("/home/dstaudt/.ssh/known_hosts");
            session.setPassword("ciscopsdt");
            session.setConfig(config);
            System.out.println("\n-> Connecting session");
            session.connect();
            System.out.println("-> Session connected");

            channel = session.openChannel("shell");
            in = channel.getInputStream();
            out = channel.getOutputStream();

            System.out.println("-> Connecting channel");
            channel.connect();
            System.out.println("-> Channel connected\n");

            // Modify/add send and expect function calls here to construct
            // your desired automation scenario
            System.out.println("---------- Session output ----------");
            expect("admin:$", 30);
            send("show version active");
            expect("admin:$", 15);
            send("set cli session timeout 30");
            expect("Continue \\(y\\/n\\)\\?$", 15);
            send("y");
            expect("admin:$", 15);
            System.out.println("\n---------- Session ended ----------");

        } catch (ExpectTimedOutException e) {
            System.out.println("\n-> ERROR: timed out waiting for CLI prompt");
        } catch (IOException e) {
            System.out.println("\n->ERROR: I/O error:");
            e.printStackTrace();
        } finally {
            System.out.println("\n-> Disconnecting channel");
            channel.disconnect();
            in.close();
            out.close();
            System.out.println("-> Disconnecting session");
            session.disconnect();
        }
    }

    private static void expect(String regex, int timeout)
            throws IOException, ExpectChannelClosedException, ExpectTimedOutException {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher;
        byte[] tmp = new byte[1024];
        while (timeout > 0) {
            String data = "";
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0)
                    break;
                data = new String(tmp, 0, i);
                System.out.print(data);
            }
            matcher = pattern.matcher(data);
            if (matcher.find())
                return;
            if (channel.isClosed()) {
                if (in.available() > 0)
                    continue;
                System.out.println("Channel closed: exit-status: " + channel.getExitStatus());
                throw new ExpectChannelClosedException("Channel closed");
            }
            try {
                Thread.sleep(1000);
                --timeout;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        throw new ExpectTimedOutException();
    }

    private static void send(String command) throws IOException {
        out.write((command + "\n").getBytes());
        out.flush();
    }
}
