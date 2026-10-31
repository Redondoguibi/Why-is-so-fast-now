# Why is so fast now?

Mod de performance para **Minecraft 1.21.1 (NeoForge 21.1.x, Java 21)**.

## Recursos

| Recurso | Padrão | Observação |
|---|---|---|
| Distância de renderização adaptativa | ligado | Reduz 1 chunk por vez quando o FPS fica abaixo do alvo e restaura quando se recupera. Só cliente. |
| Ruído escalar (`scalarNoise`) | desligado | Kernel sem alocação, idêntico bit a bit ao vanilla. Ligue só após medir. |
| Ruído vetorial (`vectorNoise`) | desligado | Experimental. Exige `--add-modules=jdk.incubator.vector` na JVM; sem isso cai para escalar/vanilla. |

Tudo é configurável em `config/whyissofastnow-common.toml`.

## Garantias

- Mesma seed gera o mesmo mundo: `NoiseKernelParityTest` compara os bits com o `ImprovedNoise` vanilla.
- Antes de a config carregar, e se algo não estiver disponível, o jogo usa o caminho vanilla.

## Desenvolvimento

```text
./gradlew build      # compila e roda os testes
./gradlew runClient
```

Medições e argumentos de JVM sugeridos: veja [PERFORMANCE.md](PERFORMANCE.md).
