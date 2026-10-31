# Perfil e opções de desempenho

Para o procedimento reproduzível de captura, registro e decisão de hotspots, siga [SPARK_WORKFLOW.md](./SPARK_WORKFLOW.md). O script `scripts/Save-SparkProfile.ps1` acrescenta referências ao índice `profiles/spark/index.csv`.

## Estado das medições

| Cenário | Antes (Spark) | Depois (Spark) | Estado |
|---|---|---|---|
| Geração de chunks inexplorados | Não coletado | Não coletado | Pendente |
| Muitas entidades / ticks | Não coletado | Não coletado | Pendente |
| Alocações / GC | Não coletado | Não coletado | Pendente |

Nenhum resultado de desempenho foi medido neste ambiente. Os testes automatizados verificam apenas que os kernels escalar e vetorial retornam os mesmos bits que o `ImprovedNoise` vanilla.

## Captura comparável com Spark

Use a mesma máquina, versão do Java, modpack, seed e opções em cada captura. Para geração de chunks, use cópias equivalentes do mundo em que a área percorrida ainda não foi gerada; não compare uma região já em cache com uma região nova. Registre a configuração de renderização e o limite de FPS. Faça um perfil por cenário, sem alterar outras variáveis entre “antes” e “depois”.

No cliente NeoForge, o Spark documenta os comandos de cliente com o prefixo `/sparkc`:

```text
/sparkc profiler start --timeout 120
```

Durante os 120 segundos, reproduza somente o cenário descrito. Para amostrar alocações, faça uma captura separada:

```text
/sparkc profiler start --alloc --timeout 120
```

Em um servidor dedicado, use `/spark profiler start --timeout 120` e rode o mesmo cenário no servidor. Salve os links de resultado, a lista de mods, o sistema operacional, CPU, GPU, versão/vendor do Java, memória máxima da JVM e argumentos de inicialização.

## Argumentos de JVM

São recomendações opcionais para HotSpot Java 21; não são configurados pelo mod e não garantem menos stutter:

```text
-XX:+UseZGC -XX:+ZGenerational
```

Confira se a JVM aceita os argumentos antes de usá-los. Aumentar o heap sem necessidade pode piorar pausas e pressão de memória; não existe um valor de RAM universal.

Para permitir que a JVM resolva a Vector API incubadora do Java 21, adicione:

```text
--add-modules=jdk.incubator.vector
```

Sem esse módulo (ou se a classe vetorial não puder ser carregada), o mod não deve falhar: usa o kernel escalar se `scalarNoise` estiver habilitado; caso contrário, conserva o cálculo vanilla. A versão vetorial permanece opcional e desativada por padrão até que uma medição comparável demonstre benefício.

## Itens ainda condicionados ao perfil

- Os toggles de ruído estão desligados por padrão até haver resultado Spark de antes/depois.
- Não foi adicionado Mixin para reaproveitar listas ou posições além do ruído: o ponto escolhido deve aparecer como hotspot e ter análise de escape/concorrência. O vanilla já mantém `BlockPos.MutableBlockPos` em `BlockCollisions`.
- C++/FFM não foi implementado. Só será reavaliado se Spark mostrar uma conta numérica cara que aceite lote grande o bastante para compensar a travessia entre Java e nativo.

## Hotspots candidatos e decisão

Registre um candidato por linha. Não marque uma implementação como aprovada sem link para perfil baseline e perfil posterior comparáveis. Para otimizações existentes sem medição Spark, mantenha a decisão como pendente e os toggles desligados.

| HotPath ID | Cenário e perfil baseline | Método / evidência observada | Decisão (implementar/adiar/rejeitar) | Paridade bit a bit | Perfil depois / resultado |
|---|---|---|---|---|---|
| `noise-kernel` | Pendente; sem perfil Spark | Ainda não há evidência Spark para ruído | Pendente | Testes de equivalência existem; ganho não medido | Pendente |
|  |  |  |  |  |  |

Ao registrar evidência em código, cada `HotPath` pode retornar `Optional<HotPathEvidence>` contendo a referência do perfil, observação medida, decisão e teste de paridade. Um `Optional.empty()` significa explicitamente que não há justificativa Spark registrada; consulte todos os caminhos com `HotPathRegistry.forEach`, inclusive os que estão desativados.

## JMH de kernels

O benchmark isolado não inicia Minecraft e usa apenas as classes de ruído compartilhadas. Com Java 21:

```text
./gradlew :benchmarks:jmh
```

O JMH inclui aquecimento e várias medições próprias. A JVM é iniciada com `--add-modules=jdk.incubator.vector`; o benchmark contém um método por kernel e trabalha com lotes de 256 amostras.

| Ambiente / JVM | Kernel escalar (ns/256 amostras) | Vector API (ns/256 amostras) | Speedup | Observações |
|---|---:|---:|---:|---|
|  |  |  |  |  |
