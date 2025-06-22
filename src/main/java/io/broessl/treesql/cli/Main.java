package io.broessl.treesql.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.broessl.treesql.core.NavigableTreeNode;
import io.broessl.treesql.file.NavigableDirectory;
import io.broessl.treesql.json.NavigableJsonNode;
import io.broessl.treesql.sql.QueryParser;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException {
		Path rootPath = null;
		if (args.length != 1) {
			System.out.println(
					"treeSQL requires a root object as argument which could be a directory or file (currently only json supported).");
			System.out.println("Usage: java -jar treesql.jar <path-to-root-object>");
			rootPath = Path.of(".").toAbsolutePath().normalize();
			System.out.println("treeSQL continues with current working directory as root.");
		}
		if (rootPath == null) {
			rootPath = Path.of(args[0]);
		}
		if (!Files.exists(rootPath)) {
			System.err.println(
					"treeSQL requires a root object as argument which could be a directory or file (currently only json supported).");
			System.exit(1);
		}
		rootPath = rootPath.normalize();
		int consoleCols = readConsoleColumns();

		printBanner();

		boolean jsonFile = rootPath.toString().endsWith(".json");
		NavigableTreeNode root;
		if (jsonFile) {
			root = NavigableJsonNode.linkRoot(new ObjectMapper().readTree(rootPath.toFile()));
			System.out.printf("treeSQL> using JSON file '%s' as root%n", rootPath.toAbsolutePath());
		} else {
			root = new NavigableDirectory(rootPath);
			System.out.printf("treeSQL> using directory '%s' as root%n", rootPath.toAbsolutePath());
		}
		System.out.println(
				"treeSQL> treeSQL is ready! Try 'SELECT foo FROM \"/~foo\"' or type 'exit' when you're done.");

		try (Scanner scanner = new Scanner(System.in)) {
			while (!Thread.interrupted()) {
				System.out.printf("treeSQL> ");
				String nextLine = null;
				nextLine = scanner.nextLine();
				if (nextLine.isBlank()) {
					System.out.printf(
							"treeSQL uses '%s' as root.%nTry 'SELECT foo FROM \"/~foo\"' to list children.%n",
							rootPath);
					continue; // skip empty lines
				}
				if ("exit".equals(nextLine)) {
					System.out.println("treeSQL> Goodbye!");
					System.exit(0);
				} else {
					try {
						var query = QueryParser.parseStatement(nextLine);
						final ResultPrinter printer = new ResultPrinter(System.out::print, consoleCols);
						printer.printHeader(query.getColumnNames());
						query.execute(root).sequential()
								.takeWhile((list) -> {
									// abort if user performs input
									try {
										if (System.in.available() == 0) {
											return true;
										} else {
											System.err.println("aborting query execution due to user input");
											System.in.skip(System.in.available()); // clear buffer
											return false;
										}
									} catch (IOException e) {
										e.printStackTrace();
										return false;
									}
								})
								.forEach(row -> {
									printer.printRow(row);
								});
						printer.printFooter();
					} catch (Exception e) {
						System.err
								.println(String.format("✖ failed to perform query:\n>>> %s <<<\n%s", nextLine,
										e.getMessage()));
						// e.printStackTrace();
					}
				}
			}
		}
	}

	static final String BANNER = """
			                .+.** ...
			           .-*..****:...-*.
			        .***...-....***   .*....
			      .=.+...-..... ......*   *...*   _                  _____  ____  _
			    ...    .......-.:...**...==..*.  | |                / ____|/ __ \\| |
			     .***. .:.%%. .%:..:...........  | |_ _ __ ___  ___| (___ | |  | | |
			         .:.. ...%.%..%......-*.     | __| '__/ _ \\/ _ \\\\___ \\| |  | | |
			         ..:.*:.%-%%%%%.. ..:...     | |_| | |  __/  __/____) | |__| | |____
			             ...  %%%%.               \\__|_|  \\___|\\___|_____/ \\___\\_\\______|
			                 .-%%..
			                  *%%+.
			                 .%%%%.
			     ..*@=...  .%%%%%%%=.   ..%%%%%%...       ......
			.-%%-...%:.*%%%:%%%%%%%%%%%%%:.     .%%@:.:%%%%..  %%-..%%-..#%.....=%:=%. ..
			    .=.   .%%#-.%%%%%%%:%%.....#.     .........:. .-.... .         ...    ...
			      ...@%.  -%..%.%:#. .%. ..%..
			     .-..    %.. :.%:@......-.     ....
			    ..     .+.   .:#.....    ...   ....
			                   %..+
			                   .% .
			                    %
				""";

	private static int readConsoleColumns() throws IOException {
		try {
			return Integer.parseInt(System.getenv("COLUMNS"));
		} catch (Exception e) {
		}
		return 120; // Default value if reading fails
	}

	private static void printBanner() {
		BANNER.lines().forEach(l -> {
			System.out.println(l);
			try {
				Thread.sleep(17);
			} catch (InterruptedException e) {
				// ignore
			}
		});
		System.out.println();
	}
}
