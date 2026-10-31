# Why is so fast now?

Mod de desempenho experimental para Minecraft Java 1.21.1 e NeoForge.

## Ruído

Há um kernel escalar Java sem alocações por amostra e uma implementação opcional com a Vector API do Java 21. Ambos substituem somente o cálculo dos oito produtos escalares usados pela interpolação do ruído Perlin. A ordem das operações e a interpolação vanilla são preservadas.

As opções `scalarNoise` e `vectorNoise` ficam desativadas por padrão até que um perfil Spark e os testes de equivalência justifiquem ativá-las. Se ambas estiverem habilitadas, o kernel vetorial tem prioridade; se o módulo `jdk.incubator.vector` não estiver disponível, o mod usa o kernel escalar quando habilitado e, caso contrário, deixa o método vanilla executar.

## Desempenho adaptativo do cliente

O recurso é desligado por padrão. Quando ativado, monitora o FPS enquanto um mundo está aberto. Abaixo de 45 FPS após três amostras, reduz a distância em um chunk; acima de 58 FPS após dez amostras, recupera um chunk. Há um cooldown padrão de cinco segundos entre mudanças, e amostras na zona intermediária reduzem gradualmente os streaks.

## Compatibilidade testada

| Mod | Resultado | Versão testada / observações |
|---|---|---|
| Sodium |  |  |
| Embeddium |  |  |
| Canary |  |  |
| ModernFix |  |  |

## Diagnóstico

Use `/whyfast status` para ver os toggles e a disponibilidade da Vector API. `/whyfast benchmark` executa um teste local de 200 iterações de aquecimento e 2.000 de medição com lotes de 256 amostras; não representa uma medição Spark de geração de mundo.

## Limites e próximos passos

O resultado dos kernels é verificado bit a bit contra `ImprovedNoise` vanilla para uma seed e coordenadas determinísticas. Isso confirma a equivalência numérica, não um ganho de velocidade. Os resultados de perfil antes/depois ainda precisam ser medidos; consulte [PERFORMANCE.md](./PERFORMANCE.md).

Ainda não há otimizações de alocação por Mixin. No vanilla 1.21.1, `BlockCollisions` já reutiliza um único `MutableBlockPos` durante cada varredura; reutilizar listas sem identificar um ponto quente e suas regras de escape seria inseguro. Não há backend C++; ele só deve ser considerado se um perfil posterior demonstrar uma conta numérica cara e vetorizável em lote.

O mod não modifica dados salvos, seeds, mecânicas, ticks de entidades ou a geração de chunks. Reduzir distância de renderização pode ajudar a carga gráfica, mas não acelera o servidor ou a geração de terreno. Nenhum ganho é prometido sem medições no mesmo cenário e hardware.
