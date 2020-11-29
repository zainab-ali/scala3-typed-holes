let
  jdk = pkgs.jdk11;

  config = {
    packageOverrides = pkgs: rec {
      mill = pkgs.mill.overrideAttrs (
        old: rec {
          version = "0.9.3";

          src = pkgs.fetchurl {
            url    = "https://github.com/lihaoyi/mill/releases/download/${version}/${version}";
            sha256 = "0x9mvcm5znyi7w6cpiasj2v6f63y7d8qdck7lx03p2k6i9aa2f77";
          };

          installPhase = ''
            runHook preInstall
            install -Dm555 "$src" "$out/bin/.mill-wrapped"
            # can't use wrapProgram because it sets --argv0
            makeWrapper "$out/bin/.mill-wrapped" "$out/bin/mill" --set JAVA_HOME "${jdk}"
            runHook postInstall
          '';
        }
      );
    };
  };

  nixpkgs = builtins.fetchTarball {
    name   = "nixpkgs-unstable-2020-11-29";
    url    = "https://github.com/NixOS/nixpkgs/archive/1121b2259b7d.tar.gz";
    sha256 = "0w2i4byhfn8c9lq8a97xnix5alfandqkbyvh6lbpr9zrm63lmyip";
  };

  pkgs = import nixpkgs { inherit config; };
in
  pkgs.mkShell {
    buildInputs = [
      pkgs.mill
      pkgs.bashInteractive
    ];
  }
