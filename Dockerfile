# Adım 1: Temel Java imajını kullan
FROM eclipse-temurin:11-jdk-focal

# sbt'yi kurmak için gerekli değişkenler
ENV SBT_VERSION=1.9.9

# Adım 2: sbt'yi indir ve kur
RUN \
    apt-get update && \
    apt-get install -y curl && \
    curl -L "https://github.com/sbt/sbt/releases/download/v$SBT_VERSION/sbt-$SBT_VERSION.tgz" | tar xz -C /usr/share && \
    ln -s /usr/share/sbt/bin/sbt /usr/bin/sbt && \
    apt-get remove -y curl && apt-get autoremove -y && rm -rf /var/lib/apt/lists/*

# Adım 3: Uygulama kodunu kopyala ve derle
WORKDIR /app
COPY . .
RUN sbt compile

# Adım 4: Uygulamayı çalıştır
CMD ["sbt", "run"]
