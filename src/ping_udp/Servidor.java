package ping_udp;

import java.io.*;
import java.net.*;
import java.util.*;

/*
 * Servidor para processar requisições ping com UDP
 */
public class Servidor {
	private static final double LOSS_RATE = 0.3; // porcentagem de pacotes que
													// pode ser perdida - 3%
	private static final int AVERAGE_DELAY = 100; // milliseconds
	private static DatagramSocket socket;

	public static void main(String[] args) throws Exception {

		while (true) {

			// obter argumento da linha de comando
			// if (args.length != 1) {
			// System.out.println("Required arguments: port");
			// return;
			// }

			// int port = Integer.parseInt(args[0]);
			int port = 6543;

			// Gerador de nums aleatorios para simular perda e atrasos na rede
			Random random = new Random();

			// Cria um socket datagrama para receber e enviar pcts UDP
			// através da porta especificada na linha de comando
			socket = new DatagramSocket(port);
			System.out.println();

			byte[] buffer = new byte[1024];

			// Criar um pacote de datagrama para comportar o pacote UDP de
			// chegada.
			DatagramPacket request = new DatagramPacket(buffer, buffer.length);

			// Bloquear até que o hospedeiro receba o pacote UDP
			socket.receive(request);

			// Imprimir os dados recebidos
			printData(request);

			// Decidir se responde ou simula perda de pacotes
			if (random.nextDouble() < LOSS_RATE) {
				System.out.println("   Reply not sent.");
				continue;
			}

			// Simular atraso da rede
			Thread.sleep((int) (random.nextDouble() * 2 * AVERAGE_DELAY));

			// Enviar resposta
			InetAddress clientHost = request.getAddress();
			int clientPort = request.getPort();
			byte[] buf = request.getData();
			DatagramPacket reply = new DatagramPacket(buf, buf.length, clientHost, clientPort);
			socket.send(reply);

			System.out.println("   Reply sent.");

		}

	}

	/*
	 * Imprimir o dado de ping para o trecho de saida padrao.
	 */
	private static void printData(DatagramPacket request) throws Exception {
		// Obter referencias para a ordem de pacotes de bytes
		byte[] buf = request.getData();

		// Envolver os bytes numa cadeia de entrada vetor de bytes, de modo que
		// voce possa ler os dados como uma cadeia de bytes.
		ByteArrayInputStream bais = new ByteArrayInputStream(buf);

		// Envolver a cadeia de saída do vetor bytes num leitor de cadeia
		// de entrada, de modo que você possa ler os dados como uma cadeia de
		// caracteres
		InputStreamReader isr = new InputStreamReader(bais);

		// Envolver o leitor de cadeia de entrada num leitor com armazenagem, de
		// modo que você possa ler os dados de caracteres
		// linha a linha
		BufferedReader br = new BufferedReader(isr);

		// O dado da mensagem está contido numa única linha, então leia essa
		// linha
		String line = br.readLine();

		// Imprimir o endereço do hospedeiro e o dado recebido dele
		System.out.println("Received from " + request.getAddress().getHostAddress() + ": " + new String(line));
	}
}