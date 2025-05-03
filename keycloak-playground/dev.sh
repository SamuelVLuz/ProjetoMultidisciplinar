exec_option="$1"

if [ "$exec_option" = "reload" ]; then
    running_instance=$(docker ps --format '{{.Names}}' | grep -oP '^keycloak-playground-\K[^-]+' | head -n 1)

    if [ -z "$running_instance" ]; then
        valid_hotswap_instances=$(find . -type d -path "*/keycloak/extensions" | sed -e 's|/keycloak/extensions||' -e 's|^\./||' | tr '\n' ' ' | xargs)

        echo "Erro: nao ha nenhuma instancia do keycloak sendo executada no momento."
        echo "Experimente executar ./dev.sh"
        exit 1
    fi

    (
        echo "Compilando extensoes do Keycloak."
        cd keycloak/extensions &&
        mvn clean install -DskipTests &&
        echo "Copiando o arquivo de extensoes para o container." &&
        docker cp keycloak-playground-extensions.jar keycloak-playground-keycloak-1:/opt/keycloak/providers/ &&
        echo "Reiniciando o Keycloak e fazendo o build novamente." &&
        docker restart keycloak-playground-keycloak-1 &&
        echo "Removendo o arquivo jar local." &&
        rm -f keycloak-playground-extensions.jar
    )

    exit $?
else
    echo "Matando e removendo todos os containers para fazer um build novo..."

    if [ -n "$(docker ps -q)" ]; then
        docker kill $(docker ps -q) > /dev/null
    fi

    if [ -n "$(docker ps -a -q)" ]; then
        docker rm $(docker ps -a -q) > /dev/null
    fi

    common_compose_params="-p keycloak-playground -f docker-compose.yml"
    command="docker compose $common_compose_params build --parallel && docker compose $common_compose_params up"

    echo "Construindo e iniciando compose."
    echo "Comando: $command"

    eval "$command"
    exit $?
fi