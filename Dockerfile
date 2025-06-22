FROM container-registry.oracle.com/graalvm/native-image:21 AS graalvm
WORKDIR /build
COPY . .
RUN ./mvnw package -Pnative

FROM ubuntu:jammy AS runtime
WORKDIR /app
COPY --from=graalvm /build/target/treesql /app/treesql
RUN chmod 755 /app/treesql
ENTRYPOINT ["/app/treesql"]
