package ping_udp;

import java.io.*;
import java.net.*;
import java.util.*;

/* codigo da classe cliente encontrado na internet,
 * procurei entender o funcionamento dele. Falado para
 * o Professor Rafael.
 */

public class Cliente {
	private static final int MAX_TIMEOUT = 1000; // milliseconds

	public static void main(String[] args) throws Exception {
		// Pegar argumentos da linha de comando
		/*
		 * if (args.length != 2) { System.out.println(
		 * "Required arguments: Server port"); return; }
		 */

		// Numero da porta para acesso - pq diferente da do servidor?
		// int port = Integer.parseInt(args[1]);
		int port = 6542;

		// Servidor para ping, tem que ser Servidor.java
		InetAddress server;
		// server = InetAddress.getByName(args[0]);

		/*----------------------------------------------------*/
		// [mod] foi inserido o metodo pegar localhost o endereço do servidor
		// que está rodando na minha máquina :)
		server = InetAddress.getLocalHost();

		// Criar um socket datagrama para enviar e receber pacotes UDP
		// atraves da porta especificada na linha de comando - porta passada
		// como parâmetro
		DatagramSocket socket = new DatagramSocket(port);

		int sequence_number = 0;
		// Processing loop. -- QUANTIDADE DE PINGS
		while (sequence_number < 10) {

			Date now = new Date();

			long msSend = now.getTime();

			String str = "PING " + sequence_number + " " + msSend + " \n";

			byte[] buf = new byte[1024];

			buf = str.getBytes();

			// Criar um pacote datagrama para envio comoo um pacote UDP
			DatagramPacket ping = new DatagramPacket(buf, buf.length, server, port);

			// Enviar o datagrama Ping para o servidor especificado, no caso,
			// localhost (ver linha 33)
			socket.send(ping);

			// Tentando receber pacote - mas pode falhar (timeout)
			try {

				// Set up the timeout 1000 ms = 1 sec
				socket.setSoTimeout(MAX_TIMEOUT);

				// Set up an UPD packet for recieving
				DatagramPacket response = new DatagramPacket(new byte[1024], 1024);

				// Try to receive the response from the ping
				socket.receive(response);

				// If the response is received, the code will continue here,
				// otherwise it will continue in the catch

				// timestamp for when we received the packet
				now = new Date();
				long msReceived = now.getTime();
				// Print the packet and the delay
				printData(response, msReceived - msSend);
			} catch (IOException e) {
				// Print which packet has timed out
				System.out.println("Timeout for packet " + sequence_number);
			}
			// next packet
			sequence_number++;
		}
	}

	/*
	 * Print ping data to the standard output stream. slightly changed from
	 * PingServer
	 */
	private static void printData(DatagramPacket request, long delayTime) throws Exception {
		// Obtain references to the packet's array of bytes.
		byte[] buf = request.getData();

		// Wrap the bytes in a byte array input stream,
		// so that you can read the data as a stream of bytes.
		ByteArrayInputStream bais = new ByteArrayInputStream(buf);

		// Wrap the byte array output stream in an input stream reader,
		// so you can read the data as a stream of characters.
		InputStreamReader isr = new InputStreamReader(bais);

		// Wrap the input stream reader in a bufferred reader,
		// so you can read the character data a line at a time.
		// (A line is a sequence of chars terminated by any combination of \r
		// and \n.)
		BufferedReader br = new BufferedReader(isr);

		// The message data is contained in a single line, so read this line.
		String line = br.readLine();

		// Print host address and data received from it.
		System.out.println("Received from " + request.getAddress().getHostAddress() + ": " + new String(line)
				+ " Delay: " + delayTime);
	}
}