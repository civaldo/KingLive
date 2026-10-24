# Integração do King Live com TikTok LIVE

## Regras implementadas nesta versão

- Cada comentário recebido: **+1 ponto**.
- Cada unidade de presente recebido: **+5 pontos**, sem converter diamantes.
- Comentários que contenham “LEÃO”, “LEÕES” ou 🦁 vão para Leões.
- Comentários que contenham “DRAGÃO”, “DRAGÕES” ou 🐉 vão para Dragões.
- Comentários sem palavra de equipe vão para a equipe padrão definida por `KINGLIVE_DEFAULT_TEAM`.
- Presentes vão para a equipe padrão nesta primeira versão.
- Presentes de sequência são pontuados apenas quando o evento final é recebido, para reduzir duplicidade.

## Componentes adicionados

- `bridge/server.py`: serviço HTTP que conecta à LIVE e disponibiliza eventos em `/api/events?since=ID`.
- `bridge/requirements.txt`: dependências Python.
- `render.yaml`: configuração inicial de implantação do serviço.
- O APK tem campos para URL HTTPS da ponte e @ do perfil e consulta os eventos periodicamente.
- `.github/workflows/build.yml` mantém a compilação automática do APK em pushes para `main`/`master` e execução manual.

## Como colocar a ponte no ar

1. No Render, crie um novo serviço web a partir do repositório GitHub `civaldo/KingLive` e use a configuração do arquivo `render.yaml`.
2. Configure `KINGLIVE_DEFAULT_TEAM` como `lions` ou `dragons`.
3. Implante o serviço e copie a URL HTTPS gerada.
4. Abra o APK, cole a URL no campo de ponte, informe o @ do perfil que está ao vivo e toque em **Conectar ao TikTok LIVE**.
5. Acesse `/health` na URL da ponte para verificar o estado reportado pelo serviço.

## Endpoints

- `GET /health`: estado básico do serviço.
- `POST /api/connect` com JSON `{"username":"perfil"}`: inicia conexão com uma LIVE pública.
- `GET /api/events?since=0`: lê eventos desde o identificador informado.

## Avisos importantes

A documentação pública do TikTok não confirma uma API geral oficial para ler comentários e presentes em tempo real. A biblioteca TikTokLive é comunitária, não oficial e pode quebrar se o TikTok mudar o protocolo. A disponibilidade também depende de a conta estar ao vivo, do acesso à LIVE e do serviço de assinatura utilizado pela biblioteca.

Não coloque senhas, cookies de sessão ou chaves privadas no APK nem no GitHub. A primeira versão não usa login do TikTok e não deve receber credenciais privadas. O placar deve ser validado em uma LIVE pública antes de uso real.

A ponte armazena eventos temporariamente em memória; reiniciar o serviço limpa esse histórico. A hospedagem gratuita pode suspender o serviço quando inativo. O APK precisa da URL HTTPS de um serviço implantado para receber eventos reais; a compilação do APK, sozinha, não hospeda a ponte.
