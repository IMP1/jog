package jog;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class network {
	
	private final static String HANDSHAKE_CONNECT = "!o/#";
	private final static String HANDSHAKE_DISCONNECT = "#o/!";
	
	private static ArrayList<Server> serverInstances = new ArrayList<Server>();
	private static ArrayList<Client> clientInstances = new ArrayList<Client>();
	
	public static void dispose() {
		for (Server s : serverInstances) {
			s.quit();
		}
		for (Client c : clientInstances) {
			c.quit();
		}
	}
	
	public interface ClientEventHandler {
		public void onMessage(String message);
	}
	
	public interface ServerEventHandler {
		public void onMessage(String sender, String message);
		public void onConnect(String address);
	}
	
	public static class Client extends Thread {
		
		private Socket socket;
		private BufferedReader in;
		private BufferedWriter out;
		private ClientEventHandler handler;
		private boolean closed;
		
		private Client(String address, int port, ClientEventHandler handler) {
			super();
			try {
				socket = new Socket(address, port);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				this.handler = handler;
				closed = false;
			} catch (IOException e) {
				
			}
			super.start();
		}

		@Override
		public void run() {
			while (!closed) {
				try {
					for (String line = in.readLine(); line != null && !closed; line = in.readLine()) {
						handler.onMessage(line);
					}
				} catch (SocketException e) {
					// This is thrown when the in.readLine() is 
					// interrupted by the socket being closed.
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void send(String message) {
			try {
				out.write(message + "\r\n");
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void quit() {
			closed = true;
			send(HANDSHAKE_DISCONNECT);
			try {
				out.close();
				in.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("[jog.network] Closed client.");
		}
		
	}
	
	public static class Server extends Thread {
		
		private class ClientListener extends Thread {
			
			private String name;
			private Socket socket;
			private BufferedReader in;
			private boolean isClosed;
			
			private ClientListener(String name, Socket socket) {
				super();
				try {
					this.name = name;
					this.socket = socket;
					this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					isClosed = false;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void run() {
				while (!isClosed) {
					try {
						for (String line = in.readLine(); line != null; line = in.readLine()) {
							handler.onMessage(name, line);
							if (line.contains(HANDSHAKE_DISCONNECT)) {
								isClosed = true;
								removeClient(name);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
						
					}
				}
			}
			
		}
		
		private ServerSocket socket;
		private ArrayList<ClientListener> clientReaders;
		private HashMap<String, BufferedWriter> clientWriters;
		private ServerEventHandler handler;
		private boolean closed;
		private int port;
		
		private Server(int port, ServerEventHandler handler) {
			super();
			try {
				socket = new ServerSocket(port);
				socket.setReuseAddress(true);
				clientReaders = new ArrayList<ClientListener>();
				clientWriters = new HashMap<String, BufferedWriter>();
				this.handler = handler;
				this.port = port;
				closed = false;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			super.start();
		}
		
		public String getAddress() {
			return socket.getInetAddress().getHostAddress();
		}
		
		public int getPort() {
			return port;
		}
		
		public String[] getClients() {
			String[] clients = new String[clientReaders.size()];
			for (int i = 0; i < clients.length; i ++) {
				clients[i] = clientReaders.get(i).name;
			}
			return clients;
		}
		
		@Override
		public void run() {
			while (!closed) {
				try {
					Socket client = socket.accept();
					String name = client.getInetAddress().getHostAddress();
					BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
					ClientListener listener = new ClientListener(name, client);
					listener.start();
					clientReaders.add(listener);
					clientWriters.put(name, writer);
					System.out.println("[jog network] connected to \"" + name + "\".");
					handler.onConnect(name);
					send(name, HANDSHAKE_CONNECT);
				} catch (SocketException e) {
					
					// This is thrown when the socket.accept() is 
					// interrupted by the socket being closed.
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void nameClient(String oldName, String newName) {
			if (!clientWriters.containsKey(oldName)) return;
			clientWriters.put(newName, clientWriters.get(oldName));
			clientWriters.remove(oldName);
			for (ClientListener client : clientReaders) {
				if (client.name == oldName) client.name = newName;
			}
		}
		
		public void send(String address, String message) {
			if (!clientWriters.containsKey(address)) {
				System.err.println("[jog network] There is no client \"" + address + "\".");
				return;
			}
			try {
				clientWriters.get(address).write(message + "\r\n");
				clientWriters.get(address).flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void quit() {
			closed = true;
			try {
				for (BufferedWriter client : clientWriters.values()) {
					client.write(HANDSHAKE_DISCONNECT + "\r\n");
					client.close();
				}
				for (ClientListener client : clientReaders) {
					client.in.close();
					client.socket.close();
				}
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("[jog.network] Closed server.");
		}
		
		private void removeClient(String address) {
			System.out.println("[jog network] disconnected from \"" + address + "\".");
			try {
				clientWriters.get(address).write(HANDSHAKE_DISCONNECT + "\r\n");
			} catch (IOException e) {
				System.err.println("Could not send Disconnection Handshake.");
				e.printStackTrace();
			}
			try {
				clientWriters.get(address).close();
				for (ClientListener client : clientReaders) {
					if (client.name == address) {
						client.in.close();
						client.socket.close();
					}
				}
			} catch (IOException e) {
				System.err.println("Could not close streams and socket.");
				e.printStackTrace();
			}
		}
		
	}
	
	public static Server newServer(int port, ServerEventHandler handler) {
		Server s = new Server(port, handler); 
		serverInstances.add(s);
		return s;
	}
	
	public static Client newClient(String address, int port, ClientEventHandler handler) {
		Client c = new Client(address, port, handler);
		clientInstances.add(c);
		return c;
	}
	
}
