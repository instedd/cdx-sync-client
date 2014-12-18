require 'thread'
require 'filewatcher'

module CDXSync
  class FileWatcher
    def initialize(sync_directory, debug_paths = false)
      @jobs = Queue.new
      @inbox_directory = "#{sync_directory}/inbox"
      @watch_expression = "#{@inbox_directory}/**"
      @debug_paths = debug_paths
    end

    def watch
      enqueue_preexisting_files
      start_monitoring
      while true
        next_file = @jobs.pop
        yield next_file
        FileUtils.rm_rf next_file
      end
    end

    def ensure_sync_directory
      puts "Initializing RSync in #{@inbox_directory}"
      unless Dir.exists? @inbox_directory
        FileUtils.mkdir_p @inbox_directory
      end
    end

    private

    def enqueue_preexisting_files
      preexisting_files = Dir[@watch_expression]
      if preexisting_files.any?
        puts "Enqueuing #{preexisting_files.size} preexisting file(s)."
        preexisting_files.each do |f|
          @jobs << f
        end
      end
    end

    def start_monitoring
      Thread.start do
        puts "Monitoring of #{@watch_expression} started"
        begin
          ::FileWatcher.new([@watch_expression], @debug_paths).watch do |path, event|
            if event == :new
              puts "New file detected: #{path}."
              @jobs << path
            end
          end
        ensure
          puts 'Monitoring ended'
        end
      end
    end
  end
end
