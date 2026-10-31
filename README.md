# Why is so fast now?

Mod de desempenho experimental para Minecraft Java 1.21.1 e NeoForge.

## Ruído

Há um kernel escalar Java sem alocações por amostra e uma implementação opcional com a Vector API do Java 21. Ambos substituem somente o cálculo dos oito produtos escalares usados pela interpolação do ruído Perlin. A ordem das operações e a interpolação vanilla são preservadas.

As opções `scalarNoise` e `vectorNoise` ficam desativadas por padrão até que um perfil Spark e os testes de equivalência justifiquem ativá-las. Se ambas estiverem habilitadas, o kernel vetorial tem prioridade; se o módulo `jdk.incubator.vector` não estiver disponível, o mod usa o kernel escalar quando habilitado e, caso contrário, deixa o método vanilla executar.

## Desempenho adaptativo do cliente

O mod monitora o FPS enquanto um mundo está aberto. Se ficar abaixo do alvo por três amostras consecutivas, reduz a distância de renderização em um chunk. Quando o FPS supera o alvo mais a margem de recuperação por dez amostras, aumenta-a gradualmente até o valor preferido. O recurso pode ser desligado na tela de configuração.

## Limites e próximos passos

O resultado dos kernels é verificado bit a bit contra `ImprovedNoise` vanilla para seeds e coordenadas determinísticas. Isso confirma a equivalência numérica, não um ganho de velocidade. Os resultados de perfil antes/depois ainda precisam ser medidos; consulte [PERFORMANCE.md](./PERFORMANCE.md).

Ainda não há otimizações de alocação por Mixin. No vanilla 1.21.1, `BlockCollisions` já reutiliza um único `MutableBlockPos` durante cada varredura; reutilizar listas sem identificar um ponto quente e suas regras de escape seria inseguro. Não há backend C++; ele só deve ser considerado se um perfil posterior demonstrar uma conta numérica cara e vetorizável em lote.

O mod não modifica dados salvos, seeds, mecânicas, ticks de entidades ou a geração de chunks. Reduzir distância de renderização pode ajudar a carga gráfica, mas não acelera o servidor ou a geração de terreno. Nenhum ganho é prometido sem medições no mesmo cenário e hardware.
