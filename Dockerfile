# Build stage
FROM mcr.microsoft.com/dotnet/sdk:10.0 AS build
WORKDIR /src

# Copy csproj and restore dependencies
COPY ["King.Nexa.Platform/King.Nexa.Platform.csproj", "King.Nexa.Platform/"]
RUN dotnet restore "King.Nexa.Platform/King.Nexa.Platform.csproj"

# Copy everything else and build the app
COPY . .
WORKDIR "/src/King.Nexa.Platform"
RUN dotnet publish "King.Nexa.Platform.csproj" -c Release -o /app/publish /p:UseAppHost=false

# Runtime stage
FROM mcr.microsoft.com/dotnet/aspnet:10.0 AS final
WORKDIR /app
COPY --from=build /app/publish .
ENTRYPOINT ["dotnet", "King.Nexa.Platform.dll"]
