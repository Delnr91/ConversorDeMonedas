package com.conversor;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

public class Main {

    /**
     * Clase principal de la aplicación Conversor de Monedas.
     * Se conecta a una API externa para obtener tasas de cambio,
     * y permite al usuario realizar conversiones a través de un menú interactivo.
     */

    public static void main(String[] args) throws IOException, InterruptedException {

        /**
         * Método principal para la ejecución de la aplicación.
         */

        // Crear Scanner (se cierra automáticamente al final con try-with-resources)
        // Usaremos try-with-resources para asegurar que el Scanner se cierre
        // incluso si ocurren errores inesperados.
        try (Scanner scanner = new Scanner(System.in)) {

            // Mensaje de bienvenida inicial
            System.out.println("*************************************");
            System.out.println("¡Bienvenido al Conversor de Monedas!");
            System.out.println("*************************************");
            System.out.println("Obteniendo tasas de cambio actualizadas...");

            // Configurar acceso a la API
            String apiKey = "TU_API_KEY"; // TU API KEY
            String monedaBase = "USD";
            String urlApi = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/" + monedaBase;

            // Realizar solicitud HTTP
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlApi))
                    .GET()
                    .build();

            // Enviar la Solicitud y Obtener la Respuesta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Verificar y Procesar la Respuesta
            if (response.statusCode() == 200) {
                String jsonResponse = response.body();
                // Análisis JSON
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
                JsonObject ratesObject = jsonObject.getAsJsonObject("conversion_rates");

                // Filtrar monedas de interés
                String[] monedasDeInteres = {"ARS", "BOB", "BRL", "CLP", "COP", "USD"};
                Map<String, Double> tasasFiltradas = new HashMap<>();
                // System.out.println("Filtrando y guardando tasas de interés..."); // Mensaje opcional
                for (String moneda : monedasDeInteres) {
                    if (ratesObject.has(moneda)) {
                        tasasFiltradas.put(moneda, ratesObject.get(moneda).getAsDouble());
                        // System.out.println("  - Tasa para " + moneda + " guardada."); // Mensaje opcional
                    } else {
                        System.out.println("  - Advertencia: No se encontró tasa para " + moneda + ".");
                        // Considerar si salir, si falta una moneda esencial
                    }
                }
                System.out.println("------------------------------------");
                System.out.println("Tasas cargadas correctamente. ¡Listo para convertir!");
                System.out.println("------------------------------------");
                // System.out.println("Mapa de tasas filtradas final: " + tasasFiltradas); // opcional

                // Iniciar menú interactivo
                boolean salir = false;
                while (!salir) {
                    // Mostrar el menú de opciones
                    System.out.println("*************************************");
                    System.out.println("Menú de Conversiones:");
                    System.out.println("1) Dólar (USD) =>> Peso argentino (ARS)");
                    System.out.println("2) Peso argentino (ARS) =>> Dólar (USD)");
                    System.out.println("3) Dólar (USD) =>> Real brasileño (BRL)");
                    System.out.println("4) Real brasileño (BRL) =>> Dólar (USD)");
                    System.out.println("5) Dólar (USD) =>> Peso colombiano (COP)");
                    System.out.println("6) Peso colombiano (COP) =>> Dólar (USD)");
                    // Podríamos añadir más opciones para BOB y CLP
                    System.out.println("7) Salir");
                    System.out.println("*************************************");
                    System.out.print("Elija una opción válida: ");

                    //Leer la opción del usuario
                    int opcion = 0; // Inicializar opción
                    try {
                        opcion = scanner.nextInt(); // Leer número entero
                        scanner.nextLine(); // Recorrer el salto de línea pendiente
                    } catch (InputMismatchException e) {
                        System.out.println("------------------------------------");
                        System.out.println("Error: Opción inválida. Por favor, ingrese un número del 1 al 7.");
                        System.out.println("------------------------------------");
                        scanner.nextLine(); // Recorrer la entrada incorrecta
                        continue; // Volver al inicio del bucle while
                    }

                    // Procesar la opción seleccionada
                    if (opcion == 7) {
                        salir = true;
                        System.out.println("------------------------------------");
                        System.out.println("¡Gracias por usar el Conversor de Monedas! Saliendo...");
                        System.out.println("------------------------------------");
                    } else if (opcion >= 1 && opcion <= 6) {
                        // Definir monedas según la opción
                        String monedaOrigen = "";
                        String monedaDestino = "";
                        switch (opcion) {
                            case 1: monedaOrigen = "USD"; monedaDestino = "ARS"; break;
                            case 2: monedaOrigen = "ARS"; monedaDestino = "USD"; break;
                            case 3: monedaOrigen = "USD"; monedaDestino = "BRL"; break;
                            case 4: monedaOrigen = "BRL"; monedaDestino = "USD"; break;
                            case 5: monedaOrigen = "USD"; monedaDestino = "COP"; break;
                            case 6: monedaOrigen = "COP"; monedaDestino = "USD"; break;
                        }

                        // Pedir la cantidad a convertir
                        System.out.print("Ingrese la cantidad en " + monedaOrigen + " que desea convertir a " + monedaDestino + ": ");
                        double cantidadUsuario = 0.0;
                        try {
                            cantidadUsuario = scanner.nextDouble(); // Leer número decimal
                            scanner.nextLine(); // Recorre el salto de línea pendiente
                            if (cantidadUsuario <= 0) {
                                System.out.println("Error: La cantidad debe ser un número positivo.");
                                System.out.println("------------------------------------");
                                continue; // Volver al menú
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("------------------------------------");
                            System.out.println("Error: Cantidad inválida. Por favor, ingrese un número.");
                            System.out.println("------------------------------------");
                            scanner.nextLine(); // Recorre la entrada incorrecta
                            continue; // Volver al menú
                        }

                        // Realizar la conversión llamando al método auxiliar
                        double resultadoConversion = convertirMoneda(cantidadUsuario, monedaOrigen, monedaDestino, tasasFiltradas);

                        // Mostrar el resultado
                        System.out.println("------------------------------------");
                        if (resultadoConversion != -1.0) {
                            System.out.printf("Resultado: %.2f %s equivalen a %.2f %s%n",
                                    cantidadUsuario, monedaOrigen, resultadoConversion, monedaDestino);
                        } else {
                            // Mensaje de error si la conversión falló (ya se imprimió en convertirMoneda)
                            System.out.println("No se pudo realizar la conversión.");
                        }
                        System.out.println("------------------------------------");

                    } else {
                        // Opción numérica fuera de rango
                        System.out.println("------------------------------------");
                        System.out.println("Opción no válida. Por favor, elija un número del 1 al 7.");
                        System.out.println("------------------------------------");
                    }
                } // Fin del bucle while

            } else {
                // Si el código de estado no es 200, indicamos un error.
                System.out.println("Error Crítico: No se pudo obtener la información de tasas de cambio de la API.");
                System.out.println("Código de estado: " + response.statusCode());
                System.out.println("Respuesta recibida: " + response.body());
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Error de conexión o durante la solicitud HTTP: " + e.getMessage());
        } catch (Exception e) {
            // Captura genérica para otros posibles errores inesperados
            System.err.println("Ocurrió un error inesperado en la aplicación: " + e.getMessage());
            e.printStackTrace(); // Útil para depuración
        }
        // El Scanner se cierra automáticamente aquí gracias al try-with-resources

        System.out.println("Aplicación Conversor de Monedas terminada.");

    } //Fin del método main


    /**
     * Realiza la conversión de una cantidad de una moneda origen a una moneda destino,
     * utilizando las tasas de cambio proporcionadas (que están relativas a USD).
     *
     * @param cantidad La cantidad de dinero en la moneda origen a convertir.
     * @param monedaOrigen El código de la moneda de origen (ej. "USD", "ARS"). Debe existir en el mapa 'tasas'.
     * @param monedaDestino El código de la moneda de destino (ej. "BRL", "USD"). Debe existir en el mapa 'tasas'.
     * @param tasas Un mapa que contiene las tasas de cambio de las monedas relevantes respecto a USD.
     * @return La cantidad convertida a la moneda destino. Devuelve -1.0 si no se pueden encontrar las tasas necesarias en el mapa.
     */
    public static double convertirMoneda(double cantidad, String monedaOrigen, String monedaDestino, Map<String, Double> tasas) {
        // Paso 1: Verificar que las tasas para ambas monedas existen en el mapa.
        if (!tasas.containsKey(monedaOrigen)) {
            System.err.println("Error de conversión: Tasa para moneda origen '" + monedaOrigen + "' no encontrada.");
            return -1.0; // Indica error
        }
        if (!tasas.containsKey(monedaDestino)) {
            System.err.println("Error de conversión: Tasa para moneda destino '" + monedaDestino + "' no encontrada.");
            return -1.0; // Indica error
        }

        // Paso 2: Obtener las tasas de cambio desde el mapa (tasas relativas a USD).
        double tasaOrigenRespectoUSD = tasas.get(monedaOrigen);
        double tasaDestinoRespectoUSD = tasas.get(monedaDestino);

        // Paso 3: Calcular la conversión.
        double resultado = (cantidad / tasaOrigenRespectoUSD) * tasaDestinoRespectoUSD;

        // Paso 4: Devolver el resultado calculado.
        return resultado;
    }

}