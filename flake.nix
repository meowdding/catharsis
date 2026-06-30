{
  description = "Catharsis Flake";

  inputs = {
    nixpkgs.url = "github:NixOs/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs =
    {
      nixpkgs,
      flake-utils,
      ...
    }:
    flake-utils.lib.eachDefaultSystem (
      system:
      let
        pkgs = import nixpkgs { inherit system; };
        kotlin-lsp = pkgs.callPackage ./kotlin-lsp.nix { };
      in
      {
        devShells.default = pkgs.mkShell {
          shellHook = ''
            ln -s ${kotlin-lsp}/bin/kotlin-lsp .kotlin-lsp
          '';
          buildInputs = with pkgs; [
            nixd
            nil
            kotlin-lsp
          ];

        };
      }
    );
}
