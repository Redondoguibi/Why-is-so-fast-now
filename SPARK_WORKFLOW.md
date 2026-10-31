# Fluxo de otimização guiada por Spark

O próximo hotspot deve ser escolhido a partir de uma captura representativa. Não implemente uma otimização só porque uma classe parece alocar ou calcular muito.

## Passo a passo

1. **Prepare uma reprodução controlada.** Use a mesma versão do modpack, mundo/seed, rota ou ação, distância de renderização, limite de FPS e duração. Para geração de chunks, use cópias equivalentes com a área de teste ainda não gerada. Anote CPU, GPU, sistema operacional, Java/vendor e argumentos da JVM.
2. **Capture o perfil baseline.** No cliente NeoForge, inicie `/sparkc profiler start --timeout 120`, reproduza apenas o cenário por 120 segundos e aguarde o Spark parar e exibir o resultado. Para alocações, use uma captura separada iniciada com `/sparkc profiler start --alloc --timeout 120`. Em servidor dedicado, use `/spark` em vez de `/sparkc`. A documentação de [comandos do Spark](https://spark.lucko.me/docs/Command-Usage) lista as opções disponíveis.
3. **Guarde a referência.** Copie o URL exibido pelo Spark e registre-o no índice versionável:

   ```powershell
   .\scripts\Save-SparkProfile.ps1 `
     -Stage baseline `
     -Scenario "geração de chunks em área nova" `
     -Hotspot untriaged `
     -ProfileReference "https://spark.lucko.me/SEU_ID" `
     -Environment "Windows; CPU ...; GPU ...; Java 21 ..."
   ```

   Para salvar localmente em vez de enviar ao visualizador, inicie sem timeout (`/sparkc profiler start`), reproduza o cenário e pare com `/sparkc profiler stop --save-to-file`. Passe o caminho do arquivo em `-ProfileReference`. O script acrescenta metadados a `profiles/spark/index.csv`; não armazene ali mundos, dumps de heap nem outros dados sensíveis.
4. **Escolha um candidato com evidência.** Abra o perfil, identifique o método/caminho dominante e confira se ele pertence ao mod, ao Minecraft ou a outro mod. Registre método, porcentagem/tempo observado, URL do perfil e decisão (implementar, adiar ou rejeitar) na tabela de candidatos de [PERFORMANCE.md](./PERFORMANCE.md). Verifique também se a amostra é longa e representativa; um perfil curto ou feito durante carregamento inicial pode ser ruído.
5. **Implemente somente o candidato aceito.** Associe-o ao `HotPath` e forneça `HotPathEvidence` com referência do perfil, observação medida, decisão e teste de paridade. Se ainda não há evidência, `evidence()` deve permanecer vazio e a otimização fica desligada por padrão. O `HotPathRegistry.forEach` permite inspecionar inclusive os caminhos desativados; `forEachEnabled` continua limitado aos ativos.
6. **Prove equivalência.** Adicione teste que compare os resultados bit a bit com o caminho vanilla em seeds e entradas fixas, incluindo os limites relevantes. Não troque igualdade exata por tolerância se a exigência for preservar o mundo gerado.
7. **Rode testes e faça a captura depois.** Execute `./gradlew test` e `./gradlew build`. Repita o mesmo cenário, máquina e duração e registre o perfil como `after`:

   ```powershell
   .\scripts\Save-SparkProfile.ps1 `
     -Stage after `
     -Scenario "geração de chunks em área nova" `
     -Hotspot noise-kernel `
     -ProfileReference "https://spark.lucko.me/OUTRO_ID" `
     -Environment "Windows; CPU ...; GPU ...; Java 21 ..."
   ```

8. **Decida com o antes/depois.** Atualize `PERFORMANCE.md` com ambos os links e a medição (incluindo regressões, se houver). Mantenha a mudança somente se houver ganho reproduzível no hotspot sem violar a paridade; caso contrário, reverta ou desligue o caminho e registre a decisão.

## Limites e privacidade

O script registra URL ou caminho e metadados, não captura nem envia o perfil por conta própria. Perfis compartilhados podem expor nomes de threads, mods, sistema e cenário; revise o conteúdo antes de compartilhar o link. Nunca inclua saves, dumps de heap, tokens ou dados pessoais no repositório.
