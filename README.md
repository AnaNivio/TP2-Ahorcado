ACLARACION: En el tp, las variables intentos y vidasJugador estan inicializadas en 50...esto es para que haya un ganador y comprobar que 
ha sido cargado a la misma. Sin embargo, (como dice en el compilado) originalmente se le da cinco oportunidades a 
ambos jugadores para adivinar la palabra.


CUESTIONARIO
1.	Diferencia entre Runnable y Thread

En primer lugar, Runnable es una interfaz mientras que Thread es una clase. 
Una clase concreta podrá heredar de Thread en caso de que no tenga una herencia previa mientras que Runnable permite que la clase concreta pueda heredar de otra. 


2.	Ciclo de vida de un Thread

Cuando se instancia la clase Thread (o una subclase) se crea un nuevo Thread que está en su estado inicial. En este estado es simplemente un objeto más. No existe todavía el Thread en ejecución. El único método que puede invocarse sobre él es el método start. 

Cuando se invoca el método start sobre el Thread el sistema crea los recursos necesarios, lo planifica (le asigna prioridad) y llama al método run. En este momento el Thread está corriendo. 

Si el método run invoca internamente el método sleep o wait o el Thread tiene que esperar por una operación de entrada/salida, entonces el Thread pasa al estado 'no runnable' (no ejecutable) hasta que la condición de espera finalice. Durante este tiempo el sistema puede ceder control a otros Threads activos.

Por último cuando el método run finaliza el Thread termina y pasa a la situación 'Dead' (Muerto).


3.	Explique los métodos [start,sleep,yield,join]

void start(): usado para iniciar el cuerpo del Thread definido por el método run().

void sleep(): pone a dormir un Thread por un  tiempo mínimo especificado.

void join(): usado para esperar por el término del Thread sobre la cual el método es invocado, por ejemplo por término de método run().

void yield(): Mueve al Thread desde el estado de corriendo al final de la cola de procesos en espera por la CPU.

