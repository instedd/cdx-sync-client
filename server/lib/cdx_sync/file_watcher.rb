require 'thread'
module CDXSync
  class FileWatcher

    def initialize(sync_directory)
      @jobs = Queue.new
      @sync_directory = sync_directory
      @watch_expression = "#{@sync_directory}/*/inbox/*"
    end

    def watch
      enqueue_preexisting_files
      start_monitoring
      puts "Monitoring files in #{@sync_directory}..."
      while true
        next_file = @jobs.pop
        yield next_file
      end
    end

    def ensure_sync_directory
      puts "Initializing RSync in #{@sync_directory}"
      unless Dir.exists? @sync_directory
        FileUtils.mkdir_p @sync_directory
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
        puts 'Monitoring started....'
        ::FileWatcher.new(@sync_directory).watch do |path, event|
          puts 'File found'
          if event == :new and File.fnmatch(@watch_expression, path)
            puts "New file detected: #{path}."
            @jobs << path
          end
        end
        puts 'Monitoring ended'
      end
    end
  end
end
