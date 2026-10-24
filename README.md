# King Live

Aplicativo Android de batalha para lives, com equipes Leões 🦁 e Dragões 🐉.

## O que está implementado

- Placar de batalha com Leões e Dragões.
- Botões de demonstração para testar comentários (+1) e presentes (+5).
- Conexão do app com uma ponte HTTPS configurável.
- Ponte inicial em Python que lê eventos públicos do TikTok LIVE por meio da biblioteca comunitária TikTokLive.
- Comentário real vale +1. Comentário contendo “LEÃO/LEÕES” ou 🦁 pontua Leões; contendo “DRAGÃO/DRAGÕES” ou 🐉 pontua Dragões. Comentários sem palavra de equipe pontuam para a equipe padrão configurada na ponte.
- Presente real vale +5 por unidade; presentes de sequência são processados ao final da sequência para reduzir pontuação duplicada. A equipe padrão recebe os presentes nesta primeira versão.
- Compilação automática do APK continua configurada no GitHub Actions.

## Gerar APK pelo celular

1. Abra a aba [Actions](https://github.com/civaldo/KingLive/actions).
2. Selecione **Construir APK King Live**.
3. Abra a execução mais recente e aguarde ficar verde (**Success**).
4. Em **Artifacts**, baixe **KingLive-debug-apk**.
5. Extraia o ZIP para encontrar `app-debug.apk`.

## Preparar a ponte de eventos

A ponte precisa estar hospedada separadamente para que o APK consiga acessá-la. O arquivo `render.yaml` prepara uma implantação inicial no Render:

1. Entre em Render e escolha criar um novo serviço usando o repositório GitHub `civaldo/KingLive`.
2. Confirme o serviço definido por `render.yaml`.
3. Defina `KINGLIVE_DEFAULT_TEAM` como `lions` ou `dragons`.
4. Implante e copie a URL HTTPS pública do serviço.
5. No app King Live, cole essa URL e o @ do perfil que está ao vivo e toque em **Conectar ao TikTok LIVE**.

A camada gratuita de hospedagem pode dormir ou ter limites; a ponte não fica disponível até o serviço ser implantado. A conexão comunitária não é oficial nem garantida pelo TikTok.

## Segurança e limitações

- Não informe cookies de sessão nem senhas do TikTok.
- Use somente a URL HTTPS do seu serviço.
- A ponte é uma integração comunitária, não uma API oficial. Pode parar de funcionar caso o TikTok mude o protocolo.
- Teste em uma LIVE pública antes de confiar no placar.
- A pontuação de presentes é fixa: +5 por unidade, independentemente do valor em diamantes.

Documentação: [guia de integração](docs/INTEGRACAO_TIKTOK_LIVE.md).
