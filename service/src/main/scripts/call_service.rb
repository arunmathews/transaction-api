require 'net/https'
require 'uri'
require 'json'

BEGIN {
  CreateTransaction = Struct.new(:user_id, :merchant, :merchant_id, :price, :purchase_date, :tx_id) do
    def to_json(options)
      hashed = to_h
      hashed.map { |k, str| [k.to_s.gsub("_", "-"), str] }.to_h.to_json
    end
  end

  def make_http_call(http, req)
    http.verify_mode = OpenSSL::SSL::VERIFY_NONE if http.use_ssl?
    res = http.request(req)
    if res.code == '200'
      if res.body.size > 0
        json_string = res.body
        JSON.parse(json_string)
      end
    else
      puts "Got status: #{res.code} with message: #{res.message}"
      puts "Response body: #{res.body}"
      raise "Non success http response Exiting"
      exit 1
    end
  end

  def get_http(uri_string, params)
    uri = URI.parse(uri_string)
    if !params.nil?
      uri.query = URI.encode_www_form(params)
    end
    http = Net::HTTP.new(uri.host, uri.port)
    if uri.scheme == "https"
      http.use_ssl = true
    end
    req = Net::HTTP::Get.new(uri.request_uri)

    make_http_call(http, req)
  end

  def post_http(uri_string, json_string, params = nil)
    put_post_http(uri_string, json_string, "post", params)
  end

  def put_http(uri_string, json_string, params = nil)
    put_post_http(uri_string, json_string, "put", params)
  end

  def put_post_http(uri_string, json_string, type, params = nil)
    uri = URI.parse(uri_string)
    http = Net::HTTP.new(uri.host, uri.port)
    if !params.nil?
      uri.query = URI.encode_www_form(params)
    end
    if uri.scheme == "https"
      http.use_ssl = true
    end
    if type == "post"
      req = Net::HTTP::Post.new(uri.request_uri, initheader = {'Content-Type' =>'application/json'})
    else
      req = Net::HTTP::Patch.new(uri.request_uri, initheader = {'Content-Type' =>'application/json'})
    end
    req.body = json_string
    make_http_call(http, req)
  end

  def pputs(title, jsonable)
    puts title
    puts JSON.pretty_generate(jsonable)
  end
}

tx_file_in = ARGV[0]
uri_prefix = ARGV[1]
if tx_file_in.nil? || uri_prefix.nil?
  puts "No trans file and/or uri prefix provided. Exiting"
  exit
end
File.open(tx_file_in,'r').each do |line|
  split_line = line.split(",")
  split_items = split_line.collect {|item| item.strip}
  trans = CreateTransaction.new(split_items[0], split_items[1], split_items[2], split_items[3], split_items[4], split_items[5])
  pputs("Going to send trans", trans)
  create_trans_uri = "#{uri_prefix}/v1/transactions"
  trans_json = JSON.generate(trans)
  created_trans = post_http(create_trans_uri, trans_json)
  pputs("Created trans", created_trans)
end